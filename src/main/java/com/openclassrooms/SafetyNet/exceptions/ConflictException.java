package com.openclassrooms.SafetyNet.exceptions;


import lombok.extern.log4j.Log4j2;

/**
 * ConglictException Class
 */
@Log4j2
public class ConflictException extends RuntimeException {
    /**
     * Constructor
     *
     * @param message Exception message
     */
    public ConflictException(String message) {
        super(message);
        log.error("<exception> ConflictException : {}", message);
    }
}
