package pl.aliaksandrou.interviewee.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import lombok.extern.log4j.Log4j2;
import pl.aliaksandrou.interviewee.audiointerceptor.IAudioProcessor;
import pl.aliaksandrou.interviewee.enums.AIModel;
import pl.aliaksandrou.interviewee.enums.Language;
import pl.aliaksandrou.interviewee.enums.SpeechToTextModel;
import pl.aliaksandrou.interviewee.model.InterviewParams;
import pl.aliaksandrou.interviewee.service.KafkaService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static pl.aliaksandrou.interviewee.service.Util.getAudioProcessor;

@Log4j2
public class StartViewController {

    @FXML
    private ComboBox<String> aiModelComboBox;
    @FXML
    private ComboBox<String> speechToTextModelComboBox;
    @FXML
    private ComboBox<String> mainLanguageComboBox;
    @FXML
    private ComboBox<String> secondLanguageComboBox;
    @FXML
    private TextArea promptTextField;
    @FXML
    private TextArea tokenApiTextField;
    @FXML
    private TextArea questionTextArea;
    @FXML
    private TextArea translatedQuestionTextArea;
    @FXML
    private TextArea answerTextArea;
    @FXML
    private TextArea translatedAnswerTextArea;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Text readyText;

    private boolean isInterviewStarted = false;
    private static final String PROMPT_TXT = "prompt.txt";
    private static final String TOKEN_TXT = "token.txt";
    private static final IAudioProcessor audioProcessor = getAudioProcessor();
    private final ExecutorService executor = Executors.newFixedThreadPool(1);
    private final KafkaService kafkaService = KafkaService.getInstance();

    @FXML
    public void initialize() {
        var aiModels = Arrays.stream(AIModel.values()).map(AIModel::getLabel).toList();
        var speechToTextModels = Arrays.stream(SpeechToTextModel.values()).map(SpeechToTextModel::getLabel).toList();
        var interviewLanguages = Arrays.stream(Language.values()).map(Language::getLabel).toList();

        aiModelComboBox.getItems().addAll(aiModels);
        speechToTextModelComboBox.getItems().addAll(speechToTextModels);
        mainLanguageComboBox.getItems().addAll(interviewLanguages);
        secondLanguageComboBox.getItems().addAll(interviewLanguages);

        aiModelComboBox.setValue("Select AI Model");
        var aiModelTooltip = new Tooltip("Select the AI model to use");
        aiModelComboBox.setTooltip(aiModelTooltip);

        speechToTextModelComboBox.setValue("Select Speech To Text Model");
        var speechToTextModelTooltip = new Tooltip("Select the Speech To Text model to use");
        speechToTextModelComboBox.setTooltip(speechToTextModelTooltip);

        mainLanguageComboBox.setValue("Select Main Language");
        var mainLanguageTooltip = new Tooltip("Select the main language for the interview");
        mainLanguageComboBox.setTooltip(mainLanguageTooltip);

        secondLanguageComboBox.setValue("Select Second Language");
        var secondLanguageTooltip = new Tooltip("Select the second language for the interview");
        secondLanguageComboBox.setTooltip(secondLanguageTooltip);

        promptTextField.setText(readFile(PROMPT_TXT));
        tokenApiTextField.setText(readFile(TOKEN_TXT));

        stopButton.setDisable(true);

        executor.submit(() -> kafkaService.startKafkaBroker(
                questionTextArea,
                translatedQuestionTextArea,
                answerTextArea,
                translatedAnswerTextArea,
                readyText)
        );
    }

    @FXML
    private void startInterview() {
        if (isInterviewStarted) {
            return;
        }
        isInterviewStarted = true;
        stopButton.setDisable(false);
        startButton.setDisable(true);
        executor.submit(() -> audioProcessor.startProcessing(
                InterviewParams.builder()
                        .aIModel(aiModelComboBox.getValue())
                        .speechToTextModel(speechToTextModelComboBox.getValue())
                        .mainInterviewLanguage(mainLanguageComboBox.getValue())
                        .secondInterviewLanguage(secondLanguageComboBox.getValue())
                        .prompt(promptTextField.getText())
                        .tokenApi(tokenApiTextField.getText())
                        .build())
        );
    }

    @FXML
    private void stopInterview() {
        if (!isInterviewStarted) {
            return;
        }
        isInterviewStarted = false;
        stopButton.setDisable(true);
        startButton.setDisable(false);
        audioProcessor.stopProcessing();
        executor.submit(audioProcessor::stopProcessing);
        executor.submit(kafkaService::stopConsume);
    }

    private String readFile(String path) {
        try {
            return Files.readString(Paths.get(path));
        } catch (IOException e) {
            log.error("Error reading file: {}", path, e);
            return "";
        }
    }
}
