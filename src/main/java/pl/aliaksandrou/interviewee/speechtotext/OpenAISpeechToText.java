package pl.aliaksandrou.interviewee.speechtotext;

import okhttp3.*;
import pl.aliaksandrou.interviewee.enums.Language;

import java.io.File;
import java.io.IOException;

public class OpenAISpeechToText implements ISpeechToTextRecognizer {
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
