package pl.aliaksandrou.interviewee.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecognizedText {
    private String text;

    @JsonCreator
    public RecognizedText(@JsonProperty("text") String text) {
        this.text = text;
    }
}
