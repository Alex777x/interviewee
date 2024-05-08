package pl.aliaksandrou.interviewee.aichat;

import pl.aliaksandrou.interviewee.model.Message;

import java.io.IOException;
import java.util.LinkedList;

public interface IChatAI {
    String getAnswer(String question, LinkedList<Message> lastTenMessages, String prompt, String tokenApi) throws IOException;
    String getTranslatedQuestion(String question, String languageCode);
    String getTranslatedAnswer(String originalAnswer, String languageCode);
}