package com.sky.exception;

public class PhoneFormatException extends RuntimeException {
    public PhoneFormatException() {}
    public PhoneFormatException(String message) {
        super(message);
    }
}
