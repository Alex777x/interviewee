package pl.aliaksandrou.interviewee.config;

public class KafkaTopics {
    private KafkaTopics() {
    }

    public static final String QUESTION_TOPIC = "question";
    public static final String TRANSLATED_QUESTION_TOPIC = "translated-question";
    public static final String ANSWER_TOPIC = "answer";
    public static final String TRANSLATED_ANSWER_TOPIC = "translated-answer";
}
