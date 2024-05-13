package pl.aliaksandrou.interviewee.model;

import java.util.List;

public class ChatRequest {
    private String model;
    private List<Message> messages;

    public ChatRequest(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;
    }

    public ChatRequest() {
    }

    public String getModel() {
        return this.model;
    }

    public List<Message> getMessages() {
        return this.messages;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "ChatRequest{" +
                "model='" + model + '\'' +
                ", messages=" + messages +
                '}';
    }
}
