package pl.aliaksandrou.interviewee.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RecognizedText {
    private String text;

    @JsonCreator
    public RecognizedText(@JsonProperty("text") String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
