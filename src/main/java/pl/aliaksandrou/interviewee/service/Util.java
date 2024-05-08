package pl.aliaksandrou.interviewee.service;


import pl.aliaksandrou.interviewee.audioprocessor.IAudioProcessor;
import pl.aliaksandrou.interviewee.audioprocessor.LinuxAudioProcessor;
import pl.aliaksandrou.interviewee.audioprocessor.MacAudioProcessor;
import pl.aliaksandrou.interviewee.audioprocessor.WindowsAudioProcessor;
import pl.aliaksandrou.interviewee.enums.SpeechToTextModel;
import pl.aliaksandrou.interviewee.exceptions.OSNotSupportedException;
import pl.aliaksandrou.interviewee.speechtotext.ISpeechToTextRecognizer;
import pl.aliaksandrou.interviewee.speechtotext.OpenAISpeechToText;

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

    public static ISpeechToTextRecognizer getSpeechToTextRecognizer(String speechToTextModel) {
        if (SpeechToTextModel.OPEN_AI.getLabel().equals(speechToTextModel)) {
            return new OpenAISpeechToText();
        } else {
            throw new IllegalArgumentException("Speech to text model not supported.");
        }
    }
}
