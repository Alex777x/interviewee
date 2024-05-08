package pl.aliaksandrou.interviewee.exceptions;

public class BlackHoleNotFoundException extends RuntimeException {
    public BlackHoleNotFoundException(String message) {
        super(message);
    }
}
