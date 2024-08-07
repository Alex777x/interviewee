package pl.aliaksandrou.interviewee.aichat;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.apache.logging.log4j.Logger;
import pl.aliaksandrou.interviewee.model.ChatRequest;
import pl.aliaksandrou.interviewee.model.Message;
import pl.aliaksandrou.interviewee.model.chatgpt.ChatGPTResponse;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class implements the IChatAI interface and provides methods to interact with the ChatGPT AI model.
 */
public class ChatGPT implements IChatAI {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(ChatGPT.class);

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    /**
     * This method is used to get an answer from the ChatGPT AI model.
     *
     * @param question        The question to ask the AI model.
     * @param lastTenMessages The last ten messages in the conversation.
     * @param prompt          The prompt to use for the AI model.
     * @param tokenApi        The API token to use for authentication.
     * @param aiModel         The AI model to use.
     * @return The answer from the AI model.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String getAnswer(String question, LinkedList<Message> lastTenMessages, String prompt, String tokenApi, String aiModel) throws IOException {
        lastTenMessages.addFirst(new Message(Constants.SYSTEM, prompt));
        return getChatGPTAnswer(tokenApi, lastTenMessages, aiModel);
    }

    /**
     * This method is used to get a translated text from the ChatGPT AI model.
     *
     * @param question     The text to translate.
     * @param languageCode The language code to translate the text into.
     * @param tokenApi     The API token to use for authentication.
     * @param aiModel      The AI model to use.
     * @return The translated text.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String getTranslatedText(String question, String languageCode, String tokenApi, String aiModel) throws IOException {
        var system = new Message(Constants.SYSTEM, "Please translate this text to: " + languageCode + " language." +
                " Do not translate jargon or technical words.");
        var user = new Message("user", question);
        List<Message> messages = new LinkedList<>();
        messages.addLast(system);
        messages.addLast(user);

        return getChatGPTAnswer(tokenApi, messages, aiModel);
    }

    /**
     * This private method is used to interact with the ChatGPT AI model.
     *
     * @param tokenApi The API token to use for authentication.
     * @param messages The messages to send to the AI model.
     * @param aiModel  The AI model to use.
     * @return The response from the AI model.
     * @throws IOException If an I/O error occurs.
     */
    private String getChatGPTAnswer(String tokenApi, List<Message> messages, String aiModel) throws IOException {
        var chatRequest = new ChatRequest();
        chatRequest.setModel(aiModel);
        chatRequest.setMessages(messages);

        log.debug("Sending request to OpenAI API: {}", chatRequest);

        var objectMapper = new ObjectMapper();
        String chatRequestJson = objectMapper.writeValueAsString(chatRequest);

        var body = RequestBody.create(
                chatRequestJson,
                MediaType.parse("application/json; charset=utf-8")
        );

        var request = new Request.Builder()
                .url(Constants.API_OPENAI_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + tokenApi.trim())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                var chatGPTResponse = objectMapper.readValue(response.body().string(), ChatGPTResponse.class);
                var firstChoice = chatGPTResponse.getChoices().getFirst();
                return firstChoice.getMessage().getContent();
            } else {
                throw new IOException("Response body is null");
            }
        }
    }
}
