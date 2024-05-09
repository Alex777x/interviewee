package pl.aliaksandrou.interviewee.aichat;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import pl.aliaksandrou.interviewee.model.ChatRequest;
import pl.aliaksandrou.interviewee.model.Message;
import pl.aliaksandrou.interviewee.model.chatgpt.ChatGPTResponse;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ChatGPT implements IChatAI {

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public String getAnswer(String question, LinkedList<Message> lastTenMessages, String prompt, String tokenApi) throws IOException {
        lastTenMessages.addFirst(new Message("system", prompt));
        return getChatGPTAnswer(tokenApi, lastTenMessages);
    }

    @Override
    public String getTranslatedText(String question, String languageCode, String tokenApi) throws IOException {
        var system = new Message("system", "Please translate this text to: " + languageCode + " language.");
        var user = new Message("user", question);
        List<Message> messages = new LinkedList<>();
        messages.addLast(system);
        messages.addLast(user);

        return getChatGPTAnswer(tokenApi, messages);
    }

    private String getChatGPTAnswer(String tokenApi, List<Message> messages) throws IOException {
        var chatRequest = new ChatRequest();
        chatRequest.setModel("gpt-3.5-turbo");
        chatRequest.setMessages(messages);

        var objectMapper = new ObjectMapper();
        String chatRequestJson = objectMapper.writeValueAsString(chatRequest);

        var body = RequestBody.create(
                chatRequestJson,
                MediaType.parse("application/json; charset=utf-8")
        );

        var request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(body)
                .addHeader("Authorization", "Bearer " + tokenApi.trim())
                .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            var chatGPTResponse = objectMapper.readValue(response.body().string(), ChatGPTResponse.class);
            var firstChoice = chatGPTResponse.getChoices().getFirst();
            return firstChoice.getMessage().getContent();
        }
    }
}
