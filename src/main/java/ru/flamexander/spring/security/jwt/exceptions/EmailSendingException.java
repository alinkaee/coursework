package ru.flamexander.spring.security.jwt.exceptions;

public class EmailSendingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EmailSendingException() {
        super();
    }

    public EmailSendingException(String message) {
        super(message);
    }

    public EmailSendingException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailSendingException(Throwable cause) {
        super(cause);
    }

    protected EmailSendingException(String message, Throwable cause,
                                    boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
