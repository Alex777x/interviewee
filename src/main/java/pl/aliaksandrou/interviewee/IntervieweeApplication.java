package pl.aliaksandrou.interviewee;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;
import pl.aliaksandrou.interviewee.service.KafkaService;

import java.io.IOException;

public class IntervieweeApplication extends Application {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(IntervieweeApplication.class);

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(IntervieweeApplication.class.getResource("start-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 768);
        stage.setTitle("Interviewee !");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        var kafkaService = KafkaService.getInstance();
        super.stop();
        try {
            kafkaService.stopConsume();
            var scriptPath = "./stop-kafka.sh";
            var processBuilder = new ProcessBuilder("/bin/bash", scriptPath);
            var process = processBuilder.start();
            process.waitFor();
            System.exit(0);
        } catch (Exception e) {
            log.error("Error while stopping the Kafka", e);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
