package pl.aliaksandrou.interviewee.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import pl.aliaksandrou.interviewee.enums.AIModel;
import pl.aliaksandrou.interviewee.enums.Language;
import pl.aliaksandrou.interviewee.enums.SpeechToTextModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

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

    private boolean isInterviewStarted = false;
    private static final String PROMPT_TXT = "prompt.txt";
    private static final String TOKEN_TXT = "token.txt";
//    private static final AudioProcessor audioProcessor = GetAudioProcessor.getAudioProcessor();
//    private final ExecutorService executor = Executors.newFixedThreadPool(1);

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
    }

    @FXML
    private void startInterview() {
        if (isInterviewStarted) {
            return;
        }
        isInterviewStarted = true;
        stopButton.setDisable(false);
        startButton.setDisable(true);
//        executor.submit(() -> {
//            audioProcessor.startProcessing(new InterviewData(
//                    aiModelComboBox.getValue(),
//                    speechToTextModelComboBox.getValue(),
//                    mainLanguageComboBox.getValue(),
//                    secondLanguageComboBox.getValue(),
//                    promptTextField.getText(),
//                    tokenApiTextField.getText()
//            ));
//        });
    }

    @FXML
    private void stopInterview() {
        if (!isInterviewStarted) {
            return;
        }
        isInterviewStarted = false;
        stopButton.setDisable(true);
        startButton.setDisable(false);
//        executor.submit(() -> audioProcessor.stopProcessing());
    }

    private String readFile(String path) {
        try {
            return Files.readString(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
