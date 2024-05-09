package pl.aliaksandrou.interviewee.model.chatgpt;

import pl.aliaksandrou.interviewee.model.Message;

public class Choice {
    private String finishReason;
    private int index;
    private Message message;
    private Object logprobs;

    public Choice(String finishReason, int index, Message message, Object logprobs) {
        this.finishReason = finishReason;
        this.index = index;
        this.message = message;
        this.logprobs = logprobs;
    }

    public Choice() {
    }

    public String getFinishReason() {
        return this.finishReason;
    }

    public int getIndex() {
        return this.index;
    }

    public Message getMessage() {
        return this.message;
    }

    public Object getLogprobs() {
        return this.logprobs;
    }
}
