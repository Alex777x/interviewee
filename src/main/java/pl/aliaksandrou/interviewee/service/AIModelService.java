package pl.aliaksandrou.interviewee.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Logger;
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
        String recognizedText = null;

        try {
            var recognizedJson = speechToTextRecognizer.recognize(audioFile, interviewParams.getMainInterviewLanguage(), interviewParams.getTokenApi());
            recognizedText = new ObjectMapper().readValue(recognizedJson, RecognizedText.class).getText();
            kafkaService.produce(QUESTION_TOPIC, recognizedText);
        } catch (IOException e) {
            log.error("Error while recognizing audio file with params: {}, ERROR: {}", interviewParams, e);
        }

        if (recognizedText == null) {
            return;
        }

        var chatAI = Util.getChatAI(interviewParams.getAIModel());

        var question = recognizedText;
        addEntry(new Message("user", question));

        CompletableFuture<String> answerFuture = CompletableFuture.supplyAsync(() -> {
            String answer = null;
            try {
                var messagesCopy = new LinkedList<>(lastTenMessages);
                answer = chatAI.getAnswer(question, messagesCopy, interviewParams.getPrompt(), interviewParams.getTokenApi());
            } catch (IOException e) {
                log.error("Error while getting answer for question: {}, ERROR: {}", question, e);
            }
            kafkaService.produce(ANSWER_TOPIC, answer);
            addEntry(new Message("assistant", answer));
            return answer;
        });

        CompletableFuture.supplyAsync(() -> {
            var translatedQuestion = chatAI.getTranslatedQuestion(question, interviewParams.getSecondInterviewLanguage().getCode());
            kafkaService.produce(TRANSLATED_QUESTION_TOPIC, translatedQuestion);
            return translatedQuestion;
        });

        answerFuture.thenAcceptAsync(answer -> {
            var translatedAnswer = chatAI.getTranslatedAnswer(answer, interviewParams.getSecondInterviewLanguage().getCode());
            kafkaService.produce(TRANSLATED_ANSWER_TOPIC, translatedAnswer);
        });
    }

    private void addEntry(Message entry) {
        lastTenMessages.addLast(entry);
        if (lastTenMessages.size() > 10) {
            lastTenMessages.removeFirst();
        }
    }
}
