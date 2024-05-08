package pl.aliaksandrou.interviewee.aichat;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import pl.aliaksandrou.interviewee.model.ChatRequest;
import pl.aliaksandrou.interviewee.model.Message;
import pl.aliaksandrou.interviewee.model.chatgpt.ChatGPTResponse;

import java.io.IOException;
import java.util.LinkedList;

public class ChatGPT implements IChatAI {

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public String getAnswer(String question, LinkedList<Message> lastTenMessages, String prompt, String tokenApi) throws IOException {
        lastTenMessages.addFirst(new Message("system", prompt));
        var chatRequest = ChatRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(lastTenMessages)
                .build();

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

    @Override
    public String getTranslatedQuestion(String question, String languageCode) {
        return "";
    }

    @Override
    public String getTranslatedAnswer(String originalAnswer, String languageCode) {
        return "";
    }
}
