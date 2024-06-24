package com.epam.resourceservice.exception;

public class EmptyFileException extends RuntimeException {

    public EmptyFileException(String message) {
        super(message);
    }
}
