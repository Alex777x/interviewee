package pl.aliaksandrou.interviewee.enums;

/**
 * This enum represents the different AI models that can be used in the application.
 * Currently, it only contains one model: ChatGPT-4.
 */
public enum SpeechToTextModel {
    OPEN_AI("OpenAI");

    private final String label;

    SpeechToTextModel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
