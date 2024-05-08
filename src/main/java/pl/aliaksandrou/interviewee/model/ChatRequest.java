package pl.aliaksandrou.interviewee.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ChatRequest {
    public String model;
    public List<Message> messages;
}
