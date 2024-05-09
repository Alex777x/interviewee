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

    public InterviewParams(String aIModel, String speechToTextModel, Language mainInterviewLanguage, Language secondInterviewLanguage, String prompt, String tokenApi) {
        this.aIModel = aIModel;
        this.speechToTextModel = speechToTextModel;
        this.mainInterviewLanguage = mainInterviewLanguage;
        this.secondInterviewLanguage = secondInterviewLanguage;
        this.prompt = prompt;
        this.tokenApi = tokenApi;
    }

    public static InterviewParamsBuilder builder() {
        return new InterviewParamsBuilder();
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

    public String toString() {
        return "InterviewParams(aIModel=" + this.getAIModel() + ", speechToTextModel=" + this.getSpeechToTextModel() + ", mainInterviewLanguage=" + this.getMainInterviewLanguage() + ", secondInterviewLanguage=" + this.getSecondInterviewLanguage() + ", prompt=" + this.getPrompt() + ")";
    }

    public static class InterviewParamsBuilder {
        private String aIModel;
        private String speechToTextModel;
        private Language mainInterviewLanguage;
        private Language secondInterviewLanguage;
        private String prompt;
        private String tokenApi;

        InterviewParamsBuilder() {
        }

        public InterviewParamsBuilder aIModel(String aIModel) {
            this.aIModel = aIModel;
            return this;
        }

        public InterviewParamsBuilder speechToTextModel(String speechToTextModel) {
            this.speechToTextModel = speechToTextModel;
            return this;
        }

        public InterviewParamsBuilder mainInterviewLanguage(Language mainInterviewLanguage) {
            this.mainInterviewLanguage = mainInterviewLanguage;
            return this;
        }

        public InterviewParamsBuilder secondInterviewLanguage(Language secondInterviewLanguage) {
            this.secondInterviewLanguage = secondInterviewLanguage;
            return this;
        }

        public InterviewParamsBuilder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public InterviewParamsBuilder tokenApi(String tokenApi) {
            this.tokenApi = tokenApi;
            return this;
        }

        public InterviewParams build() {
            return new InterviewParams(this.aIModel, this.speechToTextModel, this.mainInterviewLanguage, this.secondInterviewLanguage, this.prompt, this.tokenApi);
        }

        public String toString() {
            return "InterviewParams.InterviewParamsBuilder(aIModel=" + this.aIModel + ", speechToTextModel=" + this.speechToTextModel + ", mainInterviewLanguage=" + this.mainInterviewLanguage + ", secondInterviewLanguage=" + this.secondInterviewLanguage + ", prompt=" + this.prompt + ", tokenApi=" + this.tokenApi + ")";
        }
    }
}
