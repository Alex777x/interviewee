package pl.aliaksandrou.interviewee.service;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.Logger;
import pl.aliaksandrou.interviewee.config.KafkaConsumerProperties;
import pl.aliaksandrou.interviewee.config.KafkaProducerProperties;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static pl.aliaksandrou.interviewee.config.KafkaTopics.*;

public class KafkaService {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(KafkaService.class);
    private static volatile KafkaService instance;
    private final AtomicBoolean running = new AtomicBoolean(true);

    private KafkaService() {
    }

    public static KafkaService getInstance() {
        if (instance == null) {
            synchronized (KafkaService.class) {
                if (instance == null) {
                    instance = new KafkaService();
                }
            }
        }
        return instance;
    }

    public void startKafkaBroker(TextArea questionTextArea,
                                 TextArea translatedQuestionTextArea,
                                 TextArea answerTextArea,
                                 TextArea translatedAnswerTextArea,
                                 Text readyText) {
        try {
            var scriptPath = "./start-kafka.sh";
            var processBuilder = new ProcessBuilder("/bin/bash", scriptPath);
            processBuilder.inheritIO();
            var process = processBuilder.start();
            process.waitFor();

            new Thread(() -> consume(QUESTION_TOPIC, questionTextArea)).start();
            new Thread(() -> consume(TRANSLATED_QUESTION_TOPIC, translatedQuestionTextArea)).start();
            new Thread(() -> consume(ANSWER_TOPIC, answerTextArea)).start();
            new Thread(() -> consume(TRANSLATED_ANSWER_TOPIC, translatedAnswerTextArea)).start();
            Platform.runLater(() -> readyText.setStyle("-fx-fill: green"));
        } catch (Exception e) {
            log.error("Error while starting the Kafka", e);
        }
    }

    private void consume(String topic, TextArea textArea) {
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(KafkaConsumerProperties.getInstance().getKafkaProperties())) {
            consumer.subscribe(Collections.singletonList(topic));

            while (running.get()) {
                var records = consumer.poll(Duration.ofMillis(100));
                List<String> messages = new LinkedList<>();
                for (ConsumerRecord<String, String> consumerRecord : records) {
                    var message = consumerRecord.value();
                    messages.add("\n--------------------------------------------------------------\n" + message + "\n");
                }
                Platform.runLater(() -> {
                    textArea.appendText(String.join("", messages));
                    String[] lines = textArea.getText().split("\n");
                    if (lines.length > 30) {
                        String[] last30Messages = Arrays.copyOfRange(lines, lines.length - 30, lines.length);
                        textArea.setText(String.join("\n", last30Messages));
                        textArea.setScrollTop(Double.MAX_VALUE);
                    }
                });
            }
        }
    }

    public void stopConsume() {
        running.set(false);
    }

    public void produce(String topic, String message) {
        final Producer<String, String> producer = new KafkaProducer<>(KafkaProducerProperties.getInstance().getKafkaProperties());
        producer.send(new ProducerRecord<>(topic, message));
        producer.close();
    }
}
