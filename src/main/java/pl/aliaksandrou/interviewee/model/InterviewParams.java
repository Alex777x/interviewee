package pl.aliaksandrou.interviewee.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pl.aliaksandrou.interviewee.exceptions.ParamsValidationException;

import static pl.aliaksandrou.interviewee.view.StartViewController.*;

@Builder
@Getter
@AllArgsConstructor
public class InterviewParams {
    private String aIModel;
    private String speechToTextModel;
    private String mainInterviewLanguage;
    private String secondInterviewLanguage;
    private String prompt;
    private String tokenApi;

    public void validateInterviewParams() throws ParamsValidationException {
        if (SELECT_AI_MODEL.equals(this.aIModel) || this.aIModel == null) {
            throw new ParamsValidationException("AI model parameter cannot be empty or default.");
        } else if (SELECT_SPEECH_TO_TEXT_MODEL.equals(this.speechToTextModel) || this.speechToTextModel == null) {
            throw new ParamsValidationException("Speech to text model parameter cannot be empty or default.");
        } else if (SELECT_MAIN_LANGUAGE.equals(this.mainInterviewLanguage) || this.mainInterviewLanguage == null) {
            throw new ParamsValidationException("Main interview language parameter cannot be empty or default.");
        } else if (SELECT_SECOND_LANGUAGE.equals(this.secondInterviewLanguage) || this.secondInterviewLanguage == null) {
            throw new ParamsValidationException("Second interview language parameter cannot be empty or default.");
        } else if (this.prompt == null) {
            throw new ParamsValidationException("Prompt parameter cannot be null.");
        } else if (this.tokenApi == null) {
            throw new ParamsValidationException("Token API parameter cannot be empty.");
        }
    }
}
