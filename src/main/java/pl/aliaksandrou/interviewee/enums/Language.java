package pl.aliaksandrou.interviewee.enums;

import lombok.Getter;

@Getter
public enum Language {
    ENGLISH("English"),
    POLISH("Polish"),
    MANDARIN("Mandarin"),
    RUSSIAN("Russian");

    private final String label;

    Language(String label) {
        this.label = label;
    }

}
