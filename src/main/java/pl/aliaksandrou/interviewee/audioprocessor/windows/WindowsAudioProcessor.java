package pl.aliaksandrou.interviewee.audioprocessor.windows;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.aliaksandrou.interviewee.audioprocessor.IAudioProcessor;
import pl.aliaksandrou.interviewee.model.InterviewParams;

import javax.sound.sampled.AudioFormat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class WindowsAudioProcessor implements IAudioProcessor {

    private static final int BUFFER_SIZE = 8192;
    private static final int RECORD_TIMEOUT = 3000; // in milliseconds
    private static final Logger log = LoggerFactory.getLogger(WindowsAudioProcessor.class);
    private boolean isRunning = true;

    @Override
    public void startProcessing(InterviewParams interviewParams,
                                TextArea questionTextArea,
                                TextArea translatedQuestionTextArea,
                                TextArea answerTextArea,
                                TextArea translatedAnswerTextArea) {
        log.info("Starting audio processing...");
        log.info("Initialize COM library");
        CoreAudioUtil.Ole32.INSTANCE.CoInitializeEx(null, CoreAudioUtil.Ole32.COINIT_MULTITHREADED);

        log.info("Get the default audio endpoint");
        PointerByReference pEnumerator = new PointerByReference();
        WinNT.HRESULT hr = CoreAudioUtil.Mmdeviceapi.INSTANCE.CoCreateInstance(CoreAudioUtil.Mmdeviceapi.CLSID_MMDeviceEnumerator, null, 1, CoreAudioUtil.Mmdeviceapi.IID_IMMDeviceEnumerator, pEnumerator);
        if (COMUtils.FAILED(hr)) {
            throw new RuntimeException("Failed to create MMDeviceEnumerator: " + hr.intValue());
        }
        CoreAudioUtil.IMMDeviceEnumerator deviceEnumerator = COMUtils.queryInterface(new Unknown(pEnumerator.getValue()), CoreAudioUtil.IMMDeviceEnumerator.class);

        PointerByReference pDevice = new PointerByReference();
        hr = deviceEnumerator.GetDefaultAudioEndpoint(0, 1, pDevice);
        if (COMUtils.FAILED(hr)) {
            throw new RuntimeException("Failed to get default audio endpoint: " + hr.intValue());
        }
        CoreAudioUtil.IMMDevice device = COMUtils.queryInterface(new Unknown(pDevice.getValue()), CoreAudioUtil.IMMDevice.class);

        log.info("Activate the audio client");
        PointerByReference pAudioClient = new PointerByReference();
        hr = device.Activate(new Guid.REFIID(CoreAudioUtil.Mmdeviceapi.IID_IAudioClient.getPointer()), 1, null, pAudioClient);
        if (COMUtils.FAILED(hr)) {
            throw new RuntimeException("Failed to activate audio client: " + hr.intValue());
        }
        CoreAudioUtil.IAudioClient audioClient = COMUtils.queryInterface(new Unknown(pAudioClient.getValue()), CoreAudioUtil.IAudioClient.class);

        log.info("Get the mix format");
        PointerByReference ppDeviceFormat = new PointerByReference();
        hr = audioClient.GetMixFormat(ppDeviceFormat);
        if (COMUtils.FAILED(hr)) {
            throw new RuntimeException("Failed to get mix format: " + hr.intValue());
        }
        AudioFormat format = getAudioFormat(ppDeviceFormat.getValue());

        log.info("Initialize the audio client");
        long hnsBufferDuration = 10000000L;
        hr = audioClient.Initialize(1, 2, hnsBufferDuration, 0, ppDeviceFormat.getValue(), null);
        if (COMUtils.FAILED(hr)) {
            throw new RuntimeException("Failed to initialize audio client: " + hr.intValue());
        }

        log.info("Get the buffer size");
        IntByReference pNumBufferFrames = new IntByReference();
        hr = audioClient.GetBufferSize(pNumBufferFrames);
        if (COMUtils.FAILED(hr)) {
            throw new RuntimeException("Failed to get buffer size: " + hr.intValue());
        }
        int bufferSize = pNumBufferFrames.getValue();

        log.info("Get the audio capture client");
        PointerByReference pCaptureClient = new PointerByReference();
        hr = audioClient.GetService(new Guid.REFIID(CoreAudioUtil.Mmdeviceapi.IID_IAudioCaptureClient.getPointer()), pCaptureClient);
        if (COMUtils.FAILED(hr)) {
            throw new RuntimeException("Failed to get audio capture client: " + hr.intValue());
        }
        CoreAudioUtil.IAudioCaptureClient captureClient = COMUtils.queryInterface(new Unknown(pCaptureClient.getValue()), CoreAudioUtil.IAudioCaptureClient.class);

        log.info("Start the audio client");
        hr = audioClient.Start();
        if (COMUtils.FAILED(hr)) {
            throw new RuntimeException("Failed to start audio client: " + hr.intValue());
        }

        log.info("Start recording");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < RECORD_TIMEOUT) {
            PointerByReference ppData = new PointerByReference();
            IntByReference pNumFramesToRead = new IntByReference();
            IntByReference pdwFlags = new IntByReference();
            LongByReference pu64DevicePosition = new LongByReference();
            LongByReference pu64QPCPosition = new LongByReference();

            hr = captureClient.GetBuffer(ppData, pNumFramesToRead, pdwFlags, pu64DevicePosition, pu64QPCPosition);
            if (COMUtils.FAILED(hr)) {
                throw new RuntimeException("Failed to get buffer: " + hr.intValue());
            }

            int numFramesToRead = pNumFramesToRead.getValue();
            if (numFramesToRead > 0) {
                byte[] buffer = ppData.getValue().getByteArray(0, numFramesToRead * format.getFrameSize());
                try {
                    out.write(buffer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                hr = captureClient.ReleaseBuffer(numFramesToRead);
                if (COMUtils.FAILED(hr)) {
                    throw new RuntimeException("Failed to release buffer: " + hr.intValue());
                }
            }
            CoreAudioUtil.Kernel32.INSTANCE.Sleep(10);
        }

        log.info("Stop recording");
        hr = audioClient.Stop();
        if (COMUtils.FAILED(hr)) {
            throw new RuntimeException("Failed to stop audio client: " + hr.intValue());
        }
        CoreAudioUtil.Ole32.INSTANCE.CoUninitialize();

        log.info("Save or process the recorded audio data");
        byte[] recordedAudio = out.toByteArray();
        log.info("Recorded audio data: {}", recordedAudio);
        // Here you can save or process the recorded audio data
    }

    private static AudioFormat getAudioFormat(Pointer formatPointer) {
        WAVEFORMATEX format = new WAVEFORMATEX(formatPointer);
        return new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                format.nSamplesPerSec,
                format.wBitsPerSample,
                format.nChannels,
                format.nBlockAlign,
                format.nSamplesPerSec,
                format.cbSize != 0
        );
    }

    public static class WAVEFORMATEX extends Structure {
        public short wFormatTag;
        public short nChannels;
        public int nSamplesPerSec;
        public int nAvgBytesPerSec;
        public short nBlockAlign;
        public short wBitsPerSample;
        public short cbSize;

        public WAVEFORMATEX(Pointer p) {
            super(p);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("wFormatTag", "nChannels", "nSamplesPerSec", "nAvgBytesPerSec", "nBlockAlign", "wBitsPerSample", "cbSize");
        }
    }

    @Override
    public void stopProcessing() {
        this.isRunning = false;
    }
}
