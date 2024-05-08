package pl.aliaksandrou.interviewee.speechtotext;

import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class OpenAISpeechToText implements ISpeechToTextRecognizer {
    @Override
    public String recognize(File audioFile, String language, String tokenApi) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", audioFile.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"), audioFile))
                .addFormDataPart("model", "whisper-1")
//                .addFormDataPart("prompt", "eiusmod nulla")
//                .addFormDataPart("response_format", "json")
//                .addFormDataPart("temperature", "0")
//                .addFormDataPart("language", "")
                .build();

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/audio/transcriptions")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + tokenApi.trim())
                .addHeader("Content-Type", "multipart/form-data")
                .build();
        Response response = client.newCall(request).execute();
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("https://api.openai.com/v1/audio/transcriptions"))
//                .header("Authorization", "Bearer " + tokenApi.trim())
//                .header("Content-Type", "multipart/form-data")
//                .POST(ofMimeMultipartData(
//                        Paths.get(audioFile.getAbsolutePath())
//                ))
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        return response.body();
        return response.body().string();
    }

    private static HttpRequest.BodyPublisher ofMimeMultipartData(Path filePath) throws IOException {
        var boundary = "===" + System.currentTimeMillis() + "===";

        var filePart = "Content-Disposition: form-data; name=\"" + "file" + "\"; filename=\"" + filePath.getFileName() + "\"\r\nContent-Type: application/octet-stream\r\n\r\n";
        var modelPart = "\r\n--" + boundary + "\r\nContent-Disposition: form-data; name=\"" + "model" + "\"\r\n\r\n" + "whisper-1";

        return HttpRequest.BodyPublishers.ofByteArrays(
                Arrays.asList(
                        ("--" + boundary + "\r\n" + filePart).getBytes(),
                        Files.readAllBytes(filePath),
                        (modelPart + "\r\n--" + boundary + "--").getBytes()
                )
        );
    }
}
