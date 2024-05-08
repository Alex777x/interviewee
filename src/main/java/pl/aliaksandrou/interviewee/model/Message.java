package pl.aliaksandrou.interviewee.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Message {
    public String role;
    public String content;
}
