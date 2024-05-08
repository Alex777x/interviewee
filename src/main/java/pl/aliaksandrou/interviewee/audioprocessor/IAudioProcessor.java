package pl.aliaksandrou.interviewee.audioprocessor;

import pl.aliaksandrou.interviewee.model.InterviewParams;

public interface IAudioProcessor {
    void startProcessing(InterviewParams interviewParams);

    void stopProcessing();
}
