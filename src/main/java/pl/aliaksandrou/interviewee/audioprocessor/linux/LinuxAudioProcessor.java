
package pl.aliaksandrou.interviewee.audioprocessor.linux;


import javafx.scene.control.TextArea;
import pl.aliaksandrou.interviewee.audioprocessor.IAudioProcessor;
import pl.aliaksandrou.interviewee.model.InterviewParams;

public class LinuxAudioProcessor implements IAudioProcessor {

    @Override
    public void startProcessing(InterviewParams interviewParams,
                                TextArea questionTextArea,
                                TextArea translatedQuestionTextArea,
                                TextArea answerTextArea,
                                TextArea translatedAnswerTextArea) {
        // TODO: Implement this method
    }

    @Override
    public void stopProcessing() {
        // TODO: Implement this method
    }
}
