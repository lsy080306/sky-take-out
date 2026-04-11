package com.sky.exception;

public class IdNumberFormatException extends RuntimeException {
    public IdNumberFormatException() {}
    public IdNumberFormatException(String message) {
        super(message);
    }
}
