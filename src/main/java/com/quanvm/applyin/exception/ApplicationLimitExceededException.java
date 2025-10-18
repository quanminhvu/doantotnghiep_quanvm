package com.quanvm.applyin.exception;

public class ApplicationLimitExceededException extends RuntimeException {
    public ApplicationLimitExceededException(String message) {
        super(message);
    }
}
