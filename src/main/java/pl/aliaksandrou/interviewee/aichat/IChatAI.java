package pl.aliaksandrou.interviewee.aichat;

import pl.aliaksandrou.interviewee.model.Message;

import java.io.IOException;
import java.util.LinkedList;

/**
 * This interface defines the methods that a Chat AI should implement.
 * A Chat AI is responsible for providing answers and translations based on the input.
 */
public interface IChatAI {

    /**
     * This method is used to get an answer from the AI.
     * It should be called when a question is asked to the AI.
     *
     * @param question        The question to ask the AI.
     * @param lastTenMessages The last ten messages in the conversation.
     * @param prompt          The prompt to use for the AI.
     * @param tokenApi        The API token to use for authentication.
     * @return The answer from the AI.
     * @throws IOException If an I/O error occurs.
     */
    String getAnswer(String question, LinkedList<Message> lastTenMessages, String prompt, String tokenApi) throws IOException;

    /**
     * This method is used to get a translated text from the AI.
     * It should be called when a text needs to be translated.
     *
     * @param question     The text to translate.
     * @param languageCode The language code to translate the text into.
     * @param tokenApi     The API token to use for authentication.
     * @return The translated text.
     * @throws IOException If an I/O error occurs.
     */
    String getTranslatedText(String question, String languageCode, String tokenApi) throws IOException;
}
