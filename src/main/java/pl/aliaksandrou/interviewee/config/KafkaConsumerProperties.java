package pl.aliaksandrou.interviewee.config;

import java.util.Properties;

public class KafkaConsumerProperties {

    private static KafkaConsumerProperties instance;

    private KafkaConsumerProperties() {
    }

    public static KafkaConsumerProperties getInstance() {
        if (instance == null) {
            instance = new KafkaConsumerProperties();
        }
        return instance;
    }

    public Properties getKafkaProperties() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("group.id", "interviewee");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return properties;
    }
}
