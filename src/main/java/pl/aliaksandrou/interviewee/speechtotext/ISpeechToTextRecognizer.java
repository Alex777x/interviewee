package pl.aliaksandrou.interviewee.speechtotext;

import java.io.File;
import java.io.IOException;

public interface ISpeechToTextRecognizer {
    String recognize(File audioFile, String language, String tokenApi) throws IOException, InterruptedException;
}
