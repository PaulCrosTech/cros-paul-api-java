package com.openclassrooms.SafetyNet.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * CustomApiError Class
 */
@Schema(description = "Custom API Error")
@Data
public class CustomApiError {

    @Schema(description = "HTTP status code")
    private int status;
    @Schema(description = "Error message")
    private String message;
    @Schema(description = "List of errors")
    private List<String> errors;
    @Schema(description = "Path")
    private String path;

    /**
     * Constructor
     *
     * @param status  status
     * @param message message
     * @param path    path
     * @param errors  list of errors
     */
    public CustomApiError(int status, String message, String path, List<String> errors) {
        super();
        this.status = status;
        this.message = message;
        this.path = path;
        this.errors = errors;
    }

}
