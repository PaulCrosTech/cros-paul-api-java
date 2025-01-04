package com.openclassrooms.SafetyNet.exceptions;


public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
