package pl.aliaksandrou.interviewee.enums;

public enum Language {
    ENGLISH("English"),
    POLISH("Polish"),
    MANDARIN("Mandarin"),
    RUSSIAN("Russian");

    private final String label;

    Language(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
