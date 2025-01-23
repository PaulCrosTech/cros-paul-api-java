package com.openclassrooms.SafetyNet.exceptions;

import lombok.extern.log4j.Log4j2;

/**
 * NotFoundException Class
 */
@Log4j2
public class NotFoundException extends RuntimeException {
    /**
     * Constructor
     *
     * @param message Exception message
     */
    public NotFoundException(String message) {
        super(message);
        log.error("<exception> NotFoundException : {}", message);
    }
}
