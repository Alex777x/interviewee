package pl.aliaksandrou.interviewee.aichat;

import pl.aliaksandrou.interviewee.model.Message;

import java.util.LinkedList;

public interface IChatAI {
    String getAnswer(String question, LinkedList<Message> lastTenMessages);
    String getTranslatedQuestion(String question, String languageCode);
    String getTranslatedAnswer(String originalAnswer, String languageCode);
}
