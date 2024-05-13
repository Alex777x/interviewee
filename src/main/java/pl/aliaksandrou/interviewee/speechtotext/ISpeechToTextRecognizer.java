package pl.aliaksandrou.interviewee.speechtotext;

import pl.aliaksandrou.interviewee.enums.Language;

import java.io.File;
import java.io.IOException;

/**
 * This interface represents a speech-to-text recognizer.
 * It provides a method to recognize the text in an audio file.
 */
public interface ISpeechToTextRecognizer {

    /**
     * Recognizes the text in the specified audio file.
     *
     * @param audioFile The audio file to recognize the text from.
     * @param language  The language of the audio file.
     * @param tokenApi  The API token to use for the recognition.
     * @return The recognized text.
     * @throws IOException If an I/O error occurs during the recognition.
     */
    String recognize(File audioFile, Language language, String tokenApi) throws IOException;
}
