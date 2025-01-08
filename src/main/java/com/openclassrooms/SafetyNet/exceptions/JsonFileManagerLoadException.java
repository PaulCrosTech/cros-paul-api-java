package com.openclassrooms.SafetyNet.exceptions;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class JsonFileManagerLoadException extends RuntimeException {
    public JsonFileManagerLoadException(String message) {
        super(message);
        log.error("<exception> JsonFileManagerLoadException : {}", message);
    }
}
