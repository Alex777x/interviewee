package pl.aliaksandrou.interviewee.enums;

public enum SpeechToTextModel {
    CHAT_GPT_4("ChatGPT-4");

    private final String label;

    SpeechToTextModel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
