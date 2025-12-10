package com.blue.bridge.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String ex) {
        super(ex);
    }
}
