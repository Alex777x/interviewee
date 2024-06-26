package pl.aliaksandrou.interviewee.model.chatgpt;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Usage {
    @JsonProperty("completion_tokens")
    private int completionTokens;
    @JsonProperty("prompt_tokens")
    private int promptTokens;
    @JsonProperty("total_tokens")
    private int totalTokens;

    public Usage(int completionTokens, int promptTokens, int totalTokens) {
        this.completionTokens = completionTokens;
        this.promptTokens = promptTokens;
        this.totalTokens = totalTokens;
    }

    public Usage() {
    }

    public int getCompletionTokens() {
        return this.completionTokens;
    }

    public int getPromptTokens() {
        return this.promptTokens;
    }

    public int getTotalTokens() {
        return this.totalTokens;
    }
}
