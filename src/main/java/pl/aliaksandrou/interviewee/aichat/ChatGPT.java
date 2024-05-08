package pl.aliaksandrou.interviewee.aichat;

import pl.aliaksandrou.interviewee.model.ChatRequest;
import pl.aliaksandrou.interviewee.model.Message;

import java.util.LinkedList;

public class ChatGPT implements IChatAI {
    @Override
    public String getAnswer(String question, LinkedList<Message> lastTenMessages) {
        ChatRequest chatRequest = ChatRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(lastTenMessages)
                .build();
        return "";
    }

    @Override
    public String getTranslatedQuestion(String question, String languageCode) {
        return "";
    }

    @Override
    public String getTranslatedAnswer(String originalAnswer, String languageCode) {
        return "";
    }
}
