package pl.aliaksandrou.interviewee.model.chatgpt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatGPTResponse {
    private List<Choice> choices;
    private int created;
    private String id;
    private String model;
    private String object;
    private Usage usage;

    public ChatGPTResponse(List<Choice> choices, int created, String id, String model, String object, Usage usage) {
        this.choices = choices;
        this.created = created;
        this.id = id;
        this.model = model;
        this.object = object;
        this.usage = usage;
    }

    public ChatGPTResponse() {
    }

    public List<Choice> getChoices() {
        return this.choices;
    }

    public int getCreated() {
        return this.created;
    }

    public String getId() {
        return this.id;
    }

    public String getModel() {
        return this.model;
    }

    public String getObject() {
        return this.object;
    }

    public Usage getUsage() {
        return this.usage;
    }
}
