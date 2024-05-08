package pl.aliaksandrou.interviewee.speechtotext;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.nio.file.Files;

public class OpenAISpeechToText implements ISpeechToTextRecognizer {
    @Override
    public String recognize(File audioFile, String language, String tokenApi) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/audio/transcriptions"))
                .header("Authorization", "Bearer " + "Your_OPENAI_API_KEY")
                .header("Content-Type", "multipart/form-data")
                .POST(ofMimeMultipartData(
                        Paths.get(audioFile.getAbsolutePath())
                ))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
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
