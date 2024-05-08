package pl.aliaksandrou.interviewee.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import pl.aliaksandrou.interviewee.model.InterviewParams;
import pl.aliaksandrou.interviewee.model.Message;
import pl.aliaksandrou.interviewee.model.RecognizedText;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

import static pl.aliaksandrou.interviewee.config.KafkaTopics.*;

@Log4j2
public class AIModelService {

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
            log.error("Error while recognizing audio file with params: {}", interviewParams);
        }

        if (recognizedText == null) {
            return;
        }

        var chatAI = Util.getChatAI(interviewParams.getAIModel());

        var question = recognizedText;
        CompletableFuture.supplyAsync(() -> {
            var translatedQuestion = chatAI.getTranslatedQuestion(question, interviewParams.getSecondInterviewLanguage().getCode());
            kafkaService.produce(TRANSLATED_QUESTION_TOPIC, translatedQuestion);
            addEntry(new Message("user", translatedQuestion));
            return translatedQuestion;
        });

        CompletableFuture<String> answerFuture = CompletableFuture.supplyAsync(() -> {
            var answer = chatAI.getAnswer(question, lastTenMessages);
            kafkaService.produce(ANSWER_TOPIC, answer);
            addEntry(new Message("assistant", answer));
            return answer;
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
