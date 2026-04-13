package com.example.school_management.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the sum of individual marks does not match the total marks,
 * or if passing marks exceed total marks. Maps to 400 Bad Request.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidMarkingSchemeException extends RuntimeException {
    public InvalidMarkingSchemeException(String message) {
        super(message);
    }
}