package pl.aliaksandrou.interviewee.speechtotext;

import pl.aliaksandrou.interviewee.enums.Language;

import java.io.File;
import java.io.IOException;

public interface ISpeechToTextRecognizer {
    String recognize(File audioFile, Language language, String tokenApi) throws IOException;
}
