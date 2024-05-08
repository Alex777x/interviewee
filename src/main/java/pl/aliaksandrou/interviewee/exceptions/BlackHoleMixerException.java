package pl.aliaksandrou.interviewee.exceptions;

public class BlackHoleMixerException extends RuntimeException {
    public BlackHoleMixerException(String message, Exception e) {
        super(message, e);
    }
}
