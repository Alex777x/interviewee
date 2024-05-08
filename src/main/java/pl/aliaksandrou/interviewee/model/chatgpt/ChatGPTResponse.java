package pl.aliaksandrou.interviewee.model.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;

@Getter
@AllArgsConstructor
public class ChatGPTResponse {
    public ArrayList<Choice> choices;
    public int created;
    public String id;
    public String model;
    public String object;
    public Usage usage;
}
