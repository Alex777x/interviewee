package pl.aliaksandrou.interviewee.aichat;

public interface IChatAI {
    String getAnswer(String question);
    String getTranslatedQuestion(String question);
    String getTranslatedAnswer(String originalAnswer);
}
