package pl.aliaksandrou.interviewee.config;

import java.util.Properties;

public class KafkaProducerProperties {

    private static KafkaProducerProperties instance;

    private KafkaProducerProperties() {
    }

    public static KafkaProducerProperties getInstance() {
        if (instance == null) {
            instance = new KafkaProducerProperties();
        }
        return instance;
    }

    public Properties getKafkaProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }
}
