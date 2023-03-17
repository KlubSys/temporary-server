package com.klub.temporayStorageServer.app.exception;

public class ForbiddenException extends Exception{

    public ForbiddenException() {
        super();
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
