package com.klub.temporayStorageServer.app.exception;

public class BadRequestContentException extends Exception {

    public BadRequestContentException() {
        super();
    }

    public BadRequestContentException(String message) {
        super(message);
    }
}
