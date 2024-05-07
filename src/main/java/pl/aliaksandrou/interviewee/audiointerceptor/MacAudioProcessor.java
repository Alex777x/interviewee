package pl.aliaksandrou.interviewee.audiointerceptor;


import lombok.SneakyThrows;
import pl.aliaksandrou.interviewee.model.InterviewParams;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class MacAudioProcessor implements IAudioProcessor {
    private ByteArrayOutputStream bos;
    private boolean isRecording = false;
    private boolean isRunning = true;
    private int silentSamples = 0;
    private final int threshold = 200;
    private int fileNumber = 0;

    private static final float SAMPLE_RATE = 44100f;
    private static final AudioFormat FORMAT = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);

    @SneakyThrows
    @Override
    public void startProcessing(InterviewParams interviewParams) {
        try {
            Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
            Mixer.Info blackHole = null;
            for (Mixer.Info info : mixerInfos) {
                if (info.getName().equals("BlackHole 2ch")) {
                    blackHole = info;
                    break;
                }
            }
            if (blackHole != null) {
                Mixer mixer = AudioSystem.getMixer(blackHole);
                DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, FORMAT);
                TargetDataLine targetDataLine = (TargetDataLine) mixer.getLine(dataLineInfo);
                targetDataLine.open(FORMAT);
                targetDataLine.start();

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = targetDataLine.read(buffer, 0, buffer.length)) != -1 && isRunning) {
                    checkAmplitude(buffer, bytesRead);
                    if (isRecording) {
                        writeToFile(buffer, 0, bytesRead);
                    }
                }
                targetDataLine.close();
            } else {
                throw new Exception("BlackHole 2ch Device not found.");
            }
        } catch (LineUnavailableException | IOException e) {
            throw new Exception("Line unavailable.", e);
        }
    }

    @Override
    public void stopProcessing() {
        isRunning = false;
    }

    private void checkAmplitude(byte[] buffer, int bytesRead) throws IOException {
        int i = 0;
        while (i < bytesRead) {
            int sample = (buffer[i + 1] << 8) | (buffer[i] & 0xFF);
            System.out.println("Amplitude: " + sample);
            if (sample > threshold) {
                silentSamples = 0;
                if (!isRecording) {
                    startRecording();
                }
            } else {
                silentSamples++;
                if (isRecording && silentSamples > 2 * SAMPLE_RATE) {
                    stopRecording();
                }
            }
            i += 2;
        }
    }

    private void startRecording() {
        isRecording = true;
        bos = new ByteArrayOutputStream();
        silentSamples = 0;
    }

    private void stopRecording() throws IOException {
        isRecording = false;
        byte[] audioBytes = bos.toByteArray();
        AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(audioBytes), FORMAT, audioBytes.length);
        File audioFile = new File("audio" + fileNumber + ".wav");
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, audioFile);
        fileNumber++;

        processAudioFileAsync(audioFile);
    }

    private void processAudioFileAsync(File audioFile) {
        // Java does not have built-in support for coroutines like Kotlin.
        // You can use a separate thread or a thread pool to perform asynchronous operations.
        // Here is a simple example using a new thread.
        new Thread(() -> {
            // do your asynchronous operations here
            sendAudioToChatGPT4(audioFile);
        }).start();
    }

    private void sendAudioToChatGPT4(File audioFile) {
        // send the audio file to chatgpt-4 for speech to text conversion
    }

    private void writeToFile(byte[] buffer, int off, int len) {
        if (isRecording) {
            bos.write(buffer, off, len);
        }
    }
}
