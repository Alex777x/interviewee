package pl.aliaksandrou.interviewee.service;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import pl.aliaksandrou.interviewee.config.KafkaConsumerProperties;

import java.time.Duration;
import java.util.Collections;

import static pl.aliaksandrou.interviewee.config.KafkaTopics.*;
import static pl.aliaksandrou.interviewee.config.KafkaTopics.TRANSLATED_ANSWER_TOPIC;

@Log4j2
public class KafkaService {

    private volatile boolean running = true;

    public void startKafkaBroker(TextArea questionTextArea,
                                 TextArea translatedQuestionTextArea,
                                 TextArea answerTextArea,
                                 TextArea translatedAnswerTextArea) {
        try {
            var scriptPath = "./start-kafka.sh";
            var processBuilder = new ProcessBuilder("/bin/bash", scriptPath);
            var process = processBuilder.start();
            process.waitFor();

            new Thread(() -> consume(QUESTION_TOPIC, questionTextArea)).start();
            new Thread(() -> consume(TRANSLATED_QUESTION_TOPIC, translatedQuestionTextArea)).start();
            new Thread(() -> consume(ANSWER_TOPIC, answerTextArea)).start();
            new Thread(() -> consume(TRANSLATED_ANSWER_TOPIC, translatedAnswerTextArea)).start();
        } catch (Exception e) {
            log.error("Error while starting the Kafka", e);
        }
    }

    private void consume(String topic, TextArea textArea) {
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(new KafkaConsumerProperties().getKafkaProperties())) {
            consumer.subscribe(Collections.singletonList(topic));

            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> consumerRecord : records) {
                    String message = consumerRecord.value();
                    // Update TextArea on the JavaFX Application Thread
                    Platform.runLater(() -> textArea.setText(message));
                }
            }
        }
    }

    public void stop() {
        running = false;
    }
}
