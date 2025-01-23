package com.openclassrooms.SafetyNet.exceptions;

import lombok.extern.log4j.Log4j2;

/**
 * JsonFileManagerSaveException Class
 */
@Log4j2
public class JsonFileManagerSaveException extends RuntimeException {
    /**
     * Constructor
     *
     * @param message Exception message
     */
    public JsonFileManagerSaveException(String message) {
        super(message);
        log.error("<exception> JsonFileManagerSaveException : {}", message);
    }
}
