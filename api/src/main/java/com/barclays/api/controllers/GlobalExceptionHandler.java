package com.barclays.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.barclays.api.domain.error.ErrorResponse;
import com.barclays.api.exceptions.DuplicateResourceException;
import com.barclays.api.exceptions.ResourceNotFoundException;

// All controllers under com.barclays.api...
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        // ideally should return more information about which fields failed
        return new ErrorResponse("The request didn't supply all the necessary data");
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateResource(DuplicateResourceException ex) {

        return new ErrorResponse(ex.getMessage());

    }

    @ExceptionHandler(SecurityException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleSecurityException(SecurityException ex) {

        return new ErrorResponse(ex.getMessage());

    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFound(ResourceNotFoundException ex) {

        return new ErrorResponse(ex.getMessage());

    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {

        if (ex == null || ex.getMessage() == null) {
            return new ErrorResponse("An unexpected error occurred");
        }
        return new ErrorResponse(ex.getMessage());
    }

}
