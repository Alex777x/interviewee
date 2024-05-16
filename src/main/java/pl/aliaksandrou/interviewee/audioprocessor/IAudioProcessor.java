package pl.aliaksandrou.interviewee.audioprocessor;

import javafx.scene.control.TextArea;
import pl.aliaksandrou.interviewee.model.InterviewParams;

/**
 * This interface defines the methods that an audio processor should implement.
 * An audio processor is responsible for handling the audio input and output during an interview.
 */
public interface IAudioProcessor {

    /**
     * This method is used to stop the audio processing.
     * It should be called when the interview is finished or when the audio processing needs to be interrupted.
     */
    void stopProcessing();

    /**
     * This method is used to start the audio processing.
     * It should be called when the interview starts.
     *
     * @param interviewParams            The parameters of the interview, including the AI model, speech-to-text model, and languages.
     * @param questionTextArea           The TextArea where the questions from the interviewer will be displayed.
     * @param translatedQuestionTextArea The TextArea where the translated questions will be displayed.
     * @param answerTextArea             The TextArea where the answers from the interviewee will be displayed.
     * @param translatedAnswerTextArea   The TextArea where the translated answers will be displayed.
     */
    void startProcessing(InterviewParams interviewParams,
                         TextArea questionTextArea,
                         TextArea translatedQuestionTextArea,
                         TextArea answerTextArea,
                         TextArea translatedAnswerTextArea);
}
