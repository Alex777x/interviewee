package pl.aliaksandrou.interviewee.model;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class InterviewParams {
    private String aIModel;
    private String speechToTextModel;
    private String mainInterviewLanguage;
    private String secondInterviewLanguage;
    private String prompt;
    private String tokenApi;
}
