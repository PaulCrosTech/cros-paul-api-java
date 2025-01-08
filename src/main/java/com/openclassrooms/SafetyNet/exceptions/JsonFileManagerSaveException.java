package com.openclassrooms.SafetyNet.exceptions;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class JsonFileManagerSaveException extends RuntimeException {
    public JsonFileManagerSaveException(String message) {
        super(message);
        log.error("<exception> JsonFileManagerSaveException : {}", message);
    }
}
