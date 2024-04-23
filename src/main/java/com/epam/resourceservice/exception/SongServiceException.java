package com.epam.resourceservice.exception;

import lombok.Getter;

@Getter
public class SongServiceException extends RuntimeException {

    int status;
    String response;

    public SongServiceException(String message, int status, String response) {
        super(message);
        this.status = status;
        this.response = response;
    }
}
