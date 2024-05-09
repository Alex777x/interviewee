package pl.aliaksandrou.interviewee.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Logger;
import pl.aliaksandrou.interviewee.aichat.IChatAI;
import pl.aliaksandrou.interviewee.model.InterviewParams;
import pl.aliaksandrou.interviewee.model.Message;
import pl.aliaksandrou.interviewee.model.RecognizedText;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

import static pl.aliaksandrou.interviewee.config.KafkaTopics.*;

public class AIModelService {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(AIModelService.class);
    private final KafkaService kafkaService = KafkaService.getInstance();
    private final LinkedList<Message> lastTenMessages = new LinkedList<>();

    public void processAudioFileAsync(File audioFile, InterviewParams interviewParams) {
        if (audioFile == null) {
            return;
        }

        var speechToTextRecognizer = Util.getSpeechToTextRecognizer(interviewParams.getSpeechToTextModel());
        CompletableFuture.supplyAsync(() -> {
            try {
                var recognizedJson = speechToTextRecognizer.recognize(audioFile, interviewParams.getMainInterviewLanguage(), interviewParams.getTokenApi());
                return new ObjectMapper().readValue(recognizedJson, RecognizedText.class).getText();
            } catch (IOException e) {
                log.error("Error while recognizing audio file with params: {}, ERROR: {}", interviewParams, e);
                return null;
            }
        }).thenApply(recognizedText -> {
            if (recognizedText != null) {
                kafkaService.produce(QUESTION_TOPIC, recognizedText);
                addEntry(new Message("user", recognizedText));
            }
            return recognizedText;
        }).thenCompose(question -> {
            if (question == null) {
                return CompletableFuture.completedFuture(null);
            }

            var chatAI = Util.getChatAI(interviewParams.getAIModel());
            var messagesCopy = new LinkedList<>(lastTenMessages);

            return CompletableFuture.supplyAsync(() -> {
                try {
                    return chatAI.getAnswer(question, messagesCopy, interviewParams.getPrompt(), interviewParams.getTokenApi());
                } catch (IOException e) {
                    log.error("Error while getting answer for question: {}, ERROR: {}", question, e);
                    return null;
                }
            }).thenApply(answer -> {
                kafkaService.produce(ANSWER_TOPIC, answer);
                addEntry(new Message("assistant", answer));
                return new String[]{question, answer};
            });
        }).thenAcceptAsync(questionAndAnswer -> {
            if (questionAndAnswer == null) return;

            var question = questionAndAnswer[0];
            var answer = questionAndAnswer[1];

            if (!interviewParams.isDoNotTranslate() && !interviewParams.getMainInterviewLanguage().equals(interviewParams.getSecondInterviewLanguage())) {
                var chatAI = Util.getChatAI(interviewParams.getAIModel());
                translateText(interviewParams, question, chatAI, TRANSLATED_QUESTION_TOPIC);
                translateText(interviewParams, answer, chatAI, TRANSLATED_ANSWER_TOPIC);
            }
        });
    }

    private void translateText(InterviewParams interviewParams, String question, IChatAI chatAI, String translatedQuestionTopic) {
        CompletableFuture.supplyAsync(() -> {
            String translatedQuestion = null;
            try {
                translatedQuestion = chatAI.getTranslatedText(question, interviewParams.getSecondInterviewLanguage().getCode(), interviewParams.getTokenApi());
            } catch (IOException e) {
                log.error("Error while translating text: {}, ERROR: {}", question, e);
            }
            kafkaService.produce(translatedQuestionTopic, translatedQuestion);
            return translatedQuestion;
        });
    }

    private void addEntry(Message entry) {
        lastTenMessages.addLast(entry);
        if (lastTenMessages.size() > 10) {
            lastTenMessages.removeFirst();
        }
    }
}
