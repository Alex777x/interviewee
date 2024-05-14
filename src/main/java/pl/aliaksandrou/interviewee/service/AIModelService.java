package pl.aliaksandrou.interviewee.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.apache.logging.log4j.Logger;
import pl.aliaksandrou.interviewee.aichat.IChatAI;
import pl.aliaksandrou.interviewee.model.InterviewParams;
import pl.aliaksandrou.interviewee.model.Message;
import pl.aliaksandrou.interviewee.model.RecognizedText;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

public class AIModelService {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(AIModelService.class);
    private final LinkedList<Message> lastTenMessages = new LinkedList<>();

    private static AIModelService instance;

    private AIModelService() {
    }

    public static synchronized AIModelService getInstance() {
        if (instance == null) {
            instance = new AIModelService();
        }
        return instance;
    }

    /**
     * This method is used to process an audio file asynchronously.
     * It recognizes the text in the audio file, gets an answer from the AI model, and translates the text if necessary.
     *
     * @param audioFile                  The audio file to process.
     * @param interviewParams            The parameters for the interview.
     * @param questionTextArea           The text area for the question.
     * @param translatedQuestionTextArea The text area for the translated question.
     * @param answerTextArea             The text area for the answer.
     * @param translatedAnswerTextArea   The text area for the translated answer.
     */
    public void processAudioFileAsync(File audioFile,
                                      InterviewParams interviewParams,
                                      TextArea questionTextArea,
                                      TextArea translatedQuestionTextArea,
                                      TextArea answerTextArea,
                                      TextArea translatedAnswerTextArea) {
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
                setTextAreaValue(questionTextArea, recognizedText);
                addEntry(new Message("user", recognizedText));
            }
            return recognizedText;
        }).thenCompose(question -> {
            if (question == null || interviewParams.isDoNotAnswer()) {
                return CompletableFuture.completedFuture(new String[]{question});
            }

            var chatAI = Util.getChatAI(interviewParams.getAIModel());
            var messagesCopy = new LinkedList<>(lastTenMessages);

            return CompletableFuture.supplyAsync(() -> {
                try {
                    return chatAI.getAnswer(
                            question,
                            messagesCopy,
                            interviewParams.getPrompt(),
                            interviewParams.getTokenApi(),
                            interviewParams.getAIModel());
                } catch (IOException e) {
                    log.error("Error while getting answer for question: {}, ERROR: {}", question, e);
                    return null;
                }
            }).thenApply(answer -> {
                setTextAreaValue(answerTextArea, answer);
                addEntry(new Message("assistant", answer));
                log.debug("Question: {}, Answer: {}", question, answer);
                return new String[]{question, answer};
            });
        }).thenAcceptAsync(questionAndAnswer -> {
            if (questionAndAnswer == null) {
                return;
            }

            var question = questionAndAnswer[0];
            log.debug("Question: {}", question);

            if (!interviewParams.isDoNotTranslate() && !interviewParams.getMainInterviewLanguage().equals(interviewParams.getSecondInterviewLanguage())) {
                var chatAI = Util.getChatAI(interviewParams.getAIModel());
                translateText(interviewParams, question, chatAI, translatedQuestionTextArea);
                if (!interviewParams.isDoNotAnswer()) {
                    var answer = questionAndAnswer[1];
                    log.debug("Answer: {}", answer);
                    translateText(interviewParams, answer, chatAI, translatedAnswerTextArea);
                }
            }
        });
    }

    private void setTextAreaValue(TextArea textArea, String textToSet) {
        var text = new StringBuilder(textToSet);
        text.append("\n-------------------------------------------------------------\n");
        Platform.runLater(() -> {
            textArea.setText(text + textArea.getText());
            String[] lines = textArea.getText().split("\n");
            if (lines.length > 30) {
                String[] last30Messages = Arrays.copyOfRange(lines, 0, 30);
                textArea.setText(String.join("\n", last30Messages));
            }
        });
    }

    private void translateText(InterviewParams interviewParams, String question, IChatAI chatAI, TextArea translatedTextArea) {
        CompletableFuture.supplyAsync(() -> {
            String translatedQuestion = null;
            try {
                translatedQuestion = chatAI.getTranslatedText(
                        question,
                        interviewParams.getSecondInterviewLanguage().getCode(),
                        interviewParams.getTokenApi(),
                        interviewParams.getAIModel()
                );
            } catch (IOException e) {
                log.error("Error while translating text: {}, ERROR: {}", question, e);
            }
            setTextAreaValue(translatedTextArea, translatedQuestion);
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
