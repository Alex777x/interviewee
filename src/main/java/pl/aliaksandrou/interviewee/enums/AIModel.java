package pl.aliaksandrou.interviewee.enums;

/**
 * This enum represents the different AI models that can be used in the application.
 * Currently, it only contains one model: ChatGPT-4.
 */
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
