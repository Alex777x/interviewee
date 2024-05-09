package pl.aliaksandrou.interviewee.model;

public class Message {
    private String role;
    private String content;

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public Message() {
    }

    public static MessageBuilder builder() {
        return new MessageBuilder();
    }

    public String getRole() {
        return this.role;
    }

    public String getContent() {
        return this.content;
    }

    public static class MessageBuilder {
        private String role;
        private String content;

        MessageBuilder() {
        }

        public MessageBuilder role(String role) {
            this.role = role;
            return this;
        }

        public MessageBuilder content(String content) {
            this.content = content;
            return this;
        }

        public Message build() {
            return new Message(this.role, this.content);
        }

        public String toString() {
            return "Message.MessageBuilder(role=" + this.role + ", content=" + this.content + ")";
        }
    }
}
