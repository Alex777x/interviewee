package pl.aliaksandrou.interviewee.speechtotext;

import java.io.File;

public interface ISpeechToTextRecognizer {
    void recognize(File audioFile, String language, String tokenApi);
}
