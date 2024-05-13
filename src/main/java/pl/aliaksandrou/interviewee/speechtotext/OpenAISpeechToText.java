package pl.aliaksandrou.interviewee.speechtotext;

import okhttp3.*;
import pl.aliaksandrou.interviewee.enums.Language;

import java.io.File;
import java.io.IOException;

/**
 * This class implements the ISpeechToTextRecognizer interface using the OpenAI API.
 * It provides a method to recognize the text in an audio file using the OpenAI API.
 */
public class OpenAISpeechToText implements ISpeechToTextRecognizer {

    /**
     * Recognizes the text in the specified audio file using the OpenAI API.
     *
     * @param audioFile The audio file to recognize the text from.
     * @param language  The language of the audio file.
     * @param tokenApi  The API token to use for the recognition.
     * @return The recognized text.
     * @throws IOException If an I/O error occurs during the recognition.
     */
    @Override
    public String recognize(File audioFile, Language language, String tokenApi) throws IOException {
        var client = new OkHttpClient().newBuilder().build();
        var body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", audioFile.getName(),
                        RequestBody.create(audioFile, MediaType.parse("application/octet-stream")))
                .addFormDataPart("model", "whisper-1")
                .addFormDataPart("language", language.getCode())
                .build();

        var request = new Request.Builder()
                .url("https://api.openai.com/v1/audio/transcriptions")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + tokenApi.trim())
                .addHeader("Content-Type", "multipart/form-data")
                .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            return response.body().string();
        }
    }
}
