package com.openclassrooms.SafetyNet.exceptions;


public class PersonConflictException extends RuntimeException {
    public PersonConflictException(String message) {
        super(message);
    }
}
