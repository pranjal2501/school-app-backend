package com.example.school_management.exception;

public class OutOfCapacityException extends RuntimeException {
    public OutOfCapacityException(String message) {
        super(message);
    }
}
