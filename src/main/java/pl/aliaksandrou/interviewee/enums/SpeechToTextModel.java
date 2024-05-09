package pl.aliaksandrou.interviewee.enums;


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
