package com.example.school_management.common.exception;

public class OutOfCapacityException extends RuntimeException {
    public OutOfCapacityException(String message) {
        super(message);
    }
}
