package pl.aliaksandrou.interviewee.enums;

import lombok.Getter;

@Getter
public enum AIModel {
    CHAT_GPT_4("ChatGPT-4");

    private final String label;

    AIModel(String label) {
        this.label = label;
    }

}
