package pl.aliaksandrou.interviewee.service;

import pl.aliaksandrou.interviewee.aichat.ChatGPT;
import pl.aliaksandrou.interviewee.aichat.IChatAI;
import pl.aliaksandrou.interviewee.audioprocessor.IAudioProcessor;
import pl.aliaksandrou.interviewee.audioprocessor.linux.LinuxAudioProcessor;
import pl.aliaksandrou.interviewee.audioprocessor.macos.MacAudioProcessor;
import pl.aliaksandrou.interviewee.audioprocessor.windows.WindowsAudioProcessor;
import pl.aliaksandrou.interviewee.enums.AIModel;
import pl.aliaksandrou.interviewee.enums.SpeechToTextModel;
import pl.aliaksandrou.interviewee.exceptions.OSNotSupportedException;
import pl.aliaksandrou.interviewee.speechtotext.ISpeechToTextRecognizer;
import pl.aliaksandrou.interviewee.speechtotext.OpenAISpeechToText;

/**
 * Utility class for getting the appropriate audio processor, speech to text recognizer, and chat AI based on
 * the system properties and provided models.
 */
public class Util {

    private Util() {
    }

    /**
     * Returns the appropriate audio processor based on the operating system.
     *
     * @return An instance of IAudioProcessor.
     * @throws OSNotSupportedException if the operating system is not supported.
     */
    public static IAudioProcessor getAudioProcessor() {
        var osName = System.getProperty("os.name");
        return switch (osName) {
            case "Linux" -> new LinuxAudioProcessor();
            case "Mac OS X" -> new MacAudioProcessor();
            case "Windows 10", "Windows 11" -> new WindowsAudioProcessor();
            default -> throw new OSNotSupportedException("OS not supported.");
        };
    }

    /**
     * Returns the appropriate speech to text recognizer based on the provided model.
     *
     * @param speechToTextModel The model of the speech to text recognizer.
     * @return An instance of ISpeechToTextRecognizer.
     * @throws IllegalArgumentException if the model is not supported.
     */
    public static ISpeechToTextRecognizer getSpeechToTextRecognizer(String speechToTextModel) {
        if (SpeechToTextModel.OPEN_AI.getLabel().equals(speechToTextModel)) {
            return new OpenAISpeechToText();
        } else {
            throw new IllegalArgumentException("Speech to text model not supported.");
        }
    }

    /**
     * Returns the appropriate chat AI based on the provided model.
     *
     * @param aiModel The model of the chat AI.
     * @return An instance of IChatAI.
     * @throws IllegalArgumentException if the model is not supported.
     */
    public static IChatAI getChatAI(String aiModel) {
        if (AIModel.GPT_4O.getLabel().equals(aiModel)) {
            return new ChatGPT();
        } else {
            throw new IllegalArgumentException("AI model not supported.");
        }
    }
}
