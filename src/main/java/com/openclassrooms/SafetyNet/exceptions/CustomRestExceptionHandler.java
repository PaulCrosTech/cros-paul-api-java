package com.openclassrooms.SafetyNet.exceptions;


import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Log4j2
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {


    /**
     * 400 BAD REQUEST
     * Handle exceptions throw by errors during object validation
     *
     * @param ex      Exception
     * @param headers HttpHeaders
     * @param status  HttpStatusCode
     * @param request WebRequest
     * @return ResponseEntity<Object>
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                  final HttpHeaders headers,
                                                                  final HttpStatusCode status,
                                                                  final WebRequest request) {
        // Construction de la liste des erreurs à partir des erreurs de validation
        final List<String> errors = new ArrayList<String>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        String path = request.getDescription(false).replace("uri=", "");

        final CustomApiError customApiError = new CustomApiError(HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                path,
                errors);
        return handleExceptionInternal(ex, customApiError, headers, status, request);
    }


    /**
     * 409 CONFLICT
     * Handle ConflictException
     *
     * @param ex      ConflictException
     * @param request WebRequest
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflictException(ConflictException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        final CustomApiError customApiError = new CustomApiError(HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                path,
                errors);
        return new ResponseEntity<>(customApiError, HttpStatus.CONFLICT);
    }
    
    /**
     * 404 NOT FOUND
     * Handle NotFoundException
     *
     * @param ex      NotFoundException
     * @param request WebRequest
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        final CustomApiError customApiError = new CustomApiError(HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                path,
                errors);
        return new ResponseEntity<>(customApiError, HttpStatus.NOT_FOUND);
    }

}
