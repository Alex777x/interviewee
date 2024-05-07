package pl.aliaksandrou.interviewee.audiointerceptor;

import pl.aliaksandrou.interviewee.model.InterviewParams;

public interface IAudioProcessor {
    void startProcessing(InterviewParams interviewParams);

    void stopProcessing();
}
