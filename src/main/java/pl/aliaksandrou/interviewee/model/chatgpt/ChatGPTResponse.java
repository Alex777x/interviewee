package pl.aliaksandrou.interviewee.model.chatgpt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatGPTResponse {
    public ArrayList<Choice> choices;
    public int created;
    public String id;
    public String model;
    public String object;
    public Usage usage;
}
