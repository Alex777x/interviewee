package pl.aliaksandrou.interviewee.model;

import pl.aliaksandrou.interviewee.enums.Language;
import pl.aliaksandrou.interviewee.exceptions.ParamsValidationException;

import static pl.aliaksandrou.interviewee.view.StartViewController.SELECT_AI_MODEL;
import static pl.aliaksandrou.interviewee.view.StartViewController.SELECT_SPEECH_TO_TEXT_MODEL;

public class InterviewParams {
    private final String aIModel;
    private final String speechToTextModel;
    private final Language mainInterviewLanguage;
    private final Language secondInterviewLanguage;
    private final String prompt;
    private final String tokenApi;
    private final boolean doNotTranslate;

    public InterviewParams(String aIModel,
                           String speechToTextModel,
                           Language mainInterviewLanguage,
                           Language secondInterviewLanguage,
                           String prompt,
                           String tokenApi,
                           boolean doNotTranslate) {
        this.aIModel = aIModel;
        this.speechToTextModel = speechToTextModel;
        this.mainInterviewLanguage = mainInterviewLanguage;
        this.secondInterviewLanguage = secondInterviewLanguage;
        this.prompt = prompt;
        this.tokenApi = tokenApi;
        this.doNotTranslate = doNotTranslate;
    }

    public void validateInterviewParams() throws ParamsValidationException {
        if (SELECT_AI_MODEL.equals(this.aIModel) || this.aIModel == null) {
            throw new ParamsValidationException("AI model parameter cannot be empty or default.");
        } else if (SELECT_SPEECH_TO_TEXT_MODEL.equals(this.speechToTextModel) || this.speechToTextModel == null) {
            throw new ParamsValidationException("Speech to text model parameter cannot be empty or default.");
        } else if (this.mainInterviewLanguage == null) {
            throw new ParamsValidationException("Main interview language parameter cannot be empty.");
        } else if (this.secondInterviewLanguage == null) {
            throw new ParamsValidationException("Second interview language parameter cannot be empty.");
        } else if (this.prompt == null) {
            throw new ParamsValidationException("Prompt parameter cannot be null.");
        } else if (this.tokenApi == null) {
            throw new ParamsValidationException("Token API parameter cannot be empty.");
        }
    }

    public String getAIModel() {
        return this.aIModel;
    }

    public String getSpeechToTextModel() {
        return this.speechToTextModel;
    }

    public Language getMainInterviewLanguage() {
        return this.mainInterviewLanguage;
    }

    public Language getSecondInterviewLanguage() {
        return this.secondInterviewLanguage;
    }

    public String getPrompt() {
        return this.prompt;
    }

    public String getTokenApi() {
        return this.tokenApi;
    }

    public boolean isDoNotTranslate() {
        return this.doNotTranslate;
    }

    public String toString() {
        return "InterviewParams(aIModel=" + this.getAIModel() + ", speechToTextModel=" + this.getSpeechToTextModel() + ", mainInterviewLanguage=" + this.getMainInterviewLanguage() + ", secondInterviewLanguage=" + this.getSecondInterviewLanguage() + ", prompt=" + this.getPrompt() + ")";
    }
}
