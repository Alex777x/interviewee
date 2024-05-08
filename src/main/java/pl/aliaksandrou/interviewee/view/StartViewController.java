package pl.aliaksandrou.interviewee.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import lombok.extern.log4j.Log4j2;
import pl.aliaksandrou.interviewee.audioprocessor.IAudioProcessor;
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

    public static final String SELECT_AI_MODEL = "Select AI Model";
    public static final String SELECT_SPEECH_TO_TEXT_MODEL = "Select Speech To Text Model";
    public static final String SELECT_MAIN_LANGUAGE = "Select Main Language";
    public static final String SELECT_SECOND_LANGUAGE = "Select Second Language";
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

        aiModelComboBox.setValue(SELECT_AI_MODEL);
        var aiModelTooltip = new Tooltip("Select the AI model to use");
        aiModelComboBox.setTooltip(aiModelTooltip);

        speechToTextModelComboBox.setValue(SELECT_SPEECH_TO_TEXT_MODEL);
        var speechToTextModelTooltip = new Tooltip("Select the Speech To Text model to use");
        speechToTextModelComboBox.setTooltip(speechToTextModelTooltip);

        mainLanguageComboBox.setValue(SELECT_MAIN_LANGUAGE);
        var mainLanguageTooltip = new Tooltip("Select the main language for the interview");
        mainLanguageComboBox.setTooltip(mainLanguageTooltip);

        secondLanguageComboBox.setValue(SELECT_SECOND_LANGUAGE);
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
        savePromptAndToken();
        var interviewParams = InterviewParams.builder()
                .aIModel(aiModelComboBox.getValue())
                .speechToTextModel(speechToTextModelComboBox.getValue())
                .mainInterviewLanguage(mainLanguageComboBox.getValue())
                .secondInterviewLanguage(secondLanguageComboBox.getValue())
                .prompt(promptTextField.getText())
                .tokenApi(tokenApiTextField.getText())
                .build();
        try {
            interviewParams.validateInterviewParams();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("An error occurred:");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
        executor.submit(() -> audioProcessor.startProcessing(interviewParams));
    }

    private void savePromptAndToken() {
        try {
            Files.writeString(Paths.get(PROMPT_TXT), promptTextField.getText());
            Files.writeString(Paths.get(TOKEN_TXT), tokenApiTextField.getText());
        } catch (IOException e) {
            log.error("Error saving prompt and token to files", e);
        }
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
