package pl.aliaksandrou.interviewee.audioprocessor;

import javax.sound.sampled.AudioFormat;

/**
 * This class contains constants used in the MacAudioProcessor class.
 */
public class AudioConstants {
    private AudioConstants() {
    }

    public static final float SAMPLE_RATE = 44100f;
    public static final AudioFormat FORMAT = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);
    public static final String BLACKHOLE_2CH = "BlackHole 2ch";
    public static final int THRESHOLD = 200;
}
