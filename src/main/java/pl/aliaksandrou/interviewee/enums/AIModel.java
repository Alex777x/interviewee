package pl.aliaksandrou.interviewee.enums;


public enum AIModel {
    CHAT_GPT_4("ChatGPT-4");

    private final String label;

    AIModel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
