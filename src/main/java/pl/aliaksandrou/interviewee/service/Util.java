package pl.aliaksandrou.interviewee.service;


import pl.aliaksandrou.interviewee.audiointerceptor.IAudioProcessor;
import pl.aliaksandrou.interviewee.audiointerceptor.LinuxAudioProcessor;
import pl.aliaksandrou.interviewee.audiointerceptor.MacAudioProcessor;
import pl.aliaksandrou.interviewee.audiointerceptor.WindowsAudioProcessor;
import pl.aliaksandrou.interviewee.exceptions.OSNotSupportedException;

public class Util {

    private Util() {
    }

    public static IAudioProcessor getAudioProcessor() {
        var osName = System.getProperty("os.name");
        return switch (osName) {
            case "Linux" -> new LinuxAudioProcessor();
            case "Mac OS X" -> new MacAudioProcessor();
            case "Windows 10", "Windows 11" -> new WindowsAudioProcessor();
            default -> throw new OSNotSupportedException("OS not supported.");
        };
    }
}
