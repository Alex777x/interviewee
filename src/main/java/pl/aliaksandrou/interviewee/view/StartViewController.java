package pl.aliaksandrou.interviewee.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.logging.log4j.Logger;
import pl.aliaksandrou.interviewee.audioprocessor.IAudioProcessor;
import pl.aliaksandrou.interviewee.enums.AIModel;
import pl.aliaksandrou.interviewee.enums.Language;
import pl.aliaksandrou.interviewee.enums.SpeechToTextModel;
import pl.aliaksandrou.interviewee.model.InterviewParams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static pl.aliaksandrou.interviewee.service.Util.getAudioProcessor;

public class StartViewController {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(StartViewController.class);
    @FXML
    private ComboBox<String> aiModelComboBox;
    @FXML
    private ComboBox<String> speechToTextModelComboBox;
    @FXML
    private ComboBox<Language> mainLanguageComboBox;
    @FXML
    private ComboBox<Language> secondLanguageComboBox;
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
    private CheckBox doNotTranslateCheckBox;
    @FXML
    private CheckBox doNotAnswerCheckBox;

    public static final String SELECT_AI_MODEL = "Select AI Model";
    public static final String SELECT_SPEECH_TO_TEXT_MODEL = "Select Speech To Text Model";
    private boolean isInterviewStarted = false;
    private static final String PROMPT_TXT = "prompt.txt";
    private static final String TOKEN_TXT = "token.txt";
    private static final IAudioProcessor audioProcessor = getAudioProcessor();
    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    @FXML
    public void initialize() {
        var aiModels = Arrays.stream(AIModel.values()).map(AIModel::getLabel).toList();
        var speechToTextModels = Arrays.stream(SpeechToTextModel.values()).map(SpeechToTextModel::getLabel).toList();

        aiModelComboBox.getItems().addAll(aiModels);
        speechToTextModelComboBox.getItems().addAll(speechToTextModels);
        mainLanguageComboBox.getItems().addAll(Language.values());
        secondLanguageComboBox.getItems().addAll(Language.values());

        aiModelComboBox.setValue(SELECT_AI_MODEL);
        var aiModelTooltip = new Tooltip("Select the AI model to use");
        aiModelComboBox.setTooltip(aiModelTooltip);

        speechToTextModelComboBox.setValue(SELECT_SPEECH_TO_TEXT_MODEL);
        var speechToTextModelTooltip = new Tooltip("Select the Speech To Text model to use");
        speechToTextModelComboBox.setTooltip(speechToTextModelTooltip);

        mainLanguageComboBox.setValue(Language.ENGLISH);
        var mainLanguageTooltip = new Tooltip("Select the main language for the interview");
        mainLanguageComboBox.setTooltip(mainLanguageTooltip);

        secondLanguageComboBox.setValue(Language.ENGLISH);
        var secondLanguageTooltip = new Tooltip("Select the second language for the interview");
        secondLanguageComboBox.setTooltip(secondLanguageTooltip);

        promptTextField.setText(readFile(PROMPT_TXT));
        tokenApiTextField.setText(readFile(TOKEN_TXT));

        questionTextArea.setPromptText("Questions here...");
        translatedQuestionTextArea.setPromptText("Translated question will appear here...");
        answerTextArea.setPromptText("AI's answer will appear here...");
        translatedAnswerTextArea.setPromptText("Translated answer will appear here...");

        stopButton.setDisable(true);
    }

    @FXML
    private void startInterview() {
        if (isInterviewStarted) {
            return;
        }
        savePromptAndToken();
        var interviewParams = new InterviewParams(
                aiModelComboBox.getValue(),
                speechToTextModelComboBox.getValue(),
                mainLanguageComboBox.getValue(),
                secondLanguageComboBox.getValue(),
                promptTextField.getText(),
                tokenApiTextField.getText(),
                doNotTranslateCheckBox.isSelected(),
                doNotAnswerCheckBox.isSelected()
        );
        try {
            interviewParams.validateInterviewParams();
            executor.submit(() -> audioProcessor.startProcessing(
                    interviewParams,
                    questionTextArea,
                    translatedQuestionTextArea,
                    answerTextArea,
                    translatedAnswerTextArea)
            );
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("An error occurred:");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
        isInterviewStarted = true;
        setStatus(true);
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
        audioProcessor.stopProcessing();
        executor.submit(audioProcessor::stopProcessing);
        isInterviewStarted = false;
        setStatus(false);
    }

    private void setStatus(boolean status) {
        stopButton.setDisable(!status);
        startButton.setDisable(status);
        aiModelComboBox.setDisable(status);
        speechToTextModelComboBox.setDisable(status);
        mainLanguageComboBox.setDisable(status);
        secondLanguageComboBox.setDisable(status);
        tokenApiTextField.setDisable(status);
        doNotTranslateCheckBox.setDisable(status);
        doNotAnswerCheckBox.setDisable(status);
    }

    private String readFile(String path) {
        try {
            return Files.readString(Paths.get(path));
        } catch (IOException e) {
            log.error("Error reading file: {}", path, e);
            return "";
        }
    }

    @FXML
    private void handleNeedTranslateCheckBoxAction() {
        boolean needTranslate = doNotTranslateCheckBox.isSelected();
        translatedQuestionTextArea.setVisible(!needTranslate);
        translatedQuestionTextArea.setManaged(!needTranslate);
        translatedAnswerTextArea.setVisible(!needTranslate);
        translatedAnswerTextArea.setManaged(!needTranslate);
    }

    @FXML
    public void handleNeedAnswerCheckBoxAction() {
        boolean needAnswer = doNotAnswerCheckBox.isSelected();
        answerTextArea.setVisible(!needAnswer);
        answerTextArea.setManaged(!needAnswer);
        translatedAnswerTextArea.setVisible(!needAnswer);
        translatedAnswerTextArea.setManaged(!needAnswer);
    }
}
