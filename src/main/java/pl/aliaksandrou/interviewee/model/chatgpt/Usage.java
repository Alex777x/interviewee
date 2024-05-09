package pl.aliaksandrou.interviewee.model.chatgpt;

public class Usage {
    private int completionTokens;
    private int promptTokens;
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
