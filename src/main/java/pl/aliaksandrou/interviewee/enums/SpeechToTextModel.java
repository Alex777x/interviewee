package pl.aliaksandrou.interviewee.enums;

import lombok.Getter;

@Getter
public enum SpeechToTextModel {
    OPEN_AI("OpenAI");

    private final String label;

    SpeechToTextModel(String label) {
        this.label = label;
    }

}
