package pl.aliaksandrou.interviewee.audioprocessor;


import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import pl.aliaksandrou.interviewee.exceptions.BlackHoleMixerException;
import pl.aliaksandrou.interviewee.exceptions.BlackHoleNotFoundException;
import pl.aliaksandrou.interviewee.model.InterviewParams;
import pl.aliaksandrou.interviewee.service.AIModelService;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class MacAudioProcessor implements IAudioProcessor {
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(MacAudioProcessor.class);
    private ByteArrayOutputStream bos;
    private boolean isRecording = false;
    private boolean isRunning = true;
    private int silentSamples = 0;
    private final int threshold = 200;
    private static final float SAMPLE_RATE = 44100f;
    private static final AudioFormat FORMAT = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);

    @Override
    public void startProcessing(InterviewParams interviewParams) {
        isRunning = true;
        try {
            Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
            Mixer.Info blackHole = getBlackHole(mixerInfos);
            if (blackHole != null) {
                TargetDataLine targetDataLine = getTargetDataLine(blackHole);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = targetDataLine.read(buffer, 0, buffer.length)) != -1 && isRunning) {
                    var audioFileByAmplitude = getAudioFileByAmplitude(buffer, bytesRead);
                    new AIModelService().processAudioFileAsync(audioFileByAmplitude, interviewParams);
                    if (isRecording) {
                        writeToFile(buffer, bytesRead);
                    }
                }
                targetDataLine.close();
            } else {
                throw new BlackHoleNotFoundException("BlackHole 2ch Device not found.");
            }
        } catch (LineUnavailableException | IOException e) {
            throw new BlackHoleMixerException("Line unavailable.", e);
        }
    }

    private static @NotNull TargetDataLine getTargetDataLine(Mixer.Info blackHole) throws LineUnavailableException {
        Mixer mixer = AudioSystem.getMixer(blackHole);
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, FORMAT);
        TargetDataLine targetDataLine = (TargetDataLine) mixer.getLine(dataLineInfo);
        targetDataLine.open(FORMAT);
        targetDataLine.start();
        return targetDataLine;
    }

    private static Mixer.Info getBlackHole(Mixer.Info[] mixerInfos) {
        Mixer.Info blackHole = null;
        for (Mixer.Info info : mixerInfos) {
            if (info.getName().equals("BlackHole 2ch")) {
                blackHole = info;
                break;
            }
        }
        return blackHole;
    }

    @Override
    public void stopProcessing() {
        isRunning = false;
    }

    //    ChatGPT magic here
    private File getAudioFileByAmplitude(byte[] buffer, int bytesRead) throws IOException {
        int i = 0;
        while (i < bytesRead) {
            int sample = (buffer[i + 1] << 8) | (buffer[i] & 0xFF);
            log.debug("Amplitude: {}", sample);
            if (sample > threshold) {
                silentSamples = 0;
                if (!isRecording) {
                    startRecording();
                }
            } else {
                silentSamples++;
                if (isRecording && silentSamples > 2 * SAMPLE_RATE) {
                    return stopRecording();
                }
            }
            i += 2;
        }
        return null;
    }

    private void startRecording() {
        isRecording = true;
        bos = new ByteArrayOutputStream();
        silentSamples = 0;
    }

    private File stopRecording() throws IOException {
        isRecording = false;
        var audioBytes = bos.toByteArray();
        var audioInputStream = new AudioInputStream(new ByteArrayInputStream(audioBytes), FORMAT, audioBytes.length);
        var audioFile = new File("audio.wav");
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, audioFile);

        return audioFile;
    }

    private void writeToFile(byte[] buffer, int len) {
        if (isRecording) {
            bos.write(buffer, 0, len);
        }
    }
}
