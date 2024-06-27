package com.epam.resourceservice.exception;

public class MetadataParserException extends RuntimeException {

    public MetadataParserException(String message, Throwable ex) {
        super(message, ex);
    }
}
