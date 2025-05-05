package com.example.imgur.exception;

public class RetriableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RetriableException(final String message) {
        this(message, null);
    }

    public RetriableException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
