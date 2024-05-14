package pl.aliaksandrou.interviewee.enums;

/**
 * This enum represents the different AI models that can be used in the application.
 * Currently, it only contains one model: ChatGPT-4.
 */
public enum AIModel {
    GPT_4O("gpt-4o"),
    GPT_4_TURBO("gpt-4-turbo"),
    GPT_3_5_TURBO("gpt-3.5-turbo");

    private final String label;

    AIModel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
