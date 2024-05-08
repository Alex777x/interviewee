package pl.aliaksandrou.interviewee.model.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Usage {
    public int completion_tokens;
    public int prompt_tokens;
    public int total_tokens;
}
