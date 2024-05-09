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

    public static ChatRequestBuilder builder() {
        return new ChatRequestBuilder();
    }

    public String getModel() {
        return this.model;
    }

    public List<Message> getMessages() {
        return this.messages;
    }

    public static class ChatRequestBuilder {
        private String model;
        private List<Message> messages;

        ChatRequestBuilder() {
        }

        public ChatRequestBuilder model(String model) {
            this.model = model;
            return this;
        }

        public ChatRequestBuilder messages(List<Message> messages) {
            this.messages = messages;
            return this;
        }

        public ChatRequest build() {
            return new ChatRequest(this.model, this.messages);
        }

        public String toString() {
            return "ChatRequest.ChatRequestBuilder(model=" + this.model + ", messages=" + this.messages + ")";
        }
    }
}
