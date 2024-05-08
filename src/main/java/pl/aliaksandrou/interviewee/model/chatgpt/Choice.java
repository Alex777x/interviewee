package pl.aliaksandrou.interviewee.model.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.aliaksandrou.interviewee.model.Message;

@Getter
@AllArgsConstructor
public class Choice {
    public String finish_reason;
    public int index;
    public Message message;
    public Object logprobs;
}
