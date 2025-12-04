package com.barclays.api.domain.error;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {


    private String error;
    private String message;
    private String path;
    private List<FieldError> fieldErrors;

    public ErrorResponse() {
    }
    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(String error,
                         String message,
                         String path,
                         List<FieldError> fieldErrors) {
        this.error = error;
        this.message = message;
        this.path = path;
        this.fieldErrors = fieldErrors;
    }

    // public static ErrorResponse of(String error,
    //                                String message,
    //                                String path,
    //                                List<FieldError> fieldErrors) {
    //     return new ErrorResponse(error,message,path,fieldErrors);
    // }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    // nested DTO for per-field validation errors
    public static class FieldError {
        private String field;
        private String message;

        public FieldError() {
        }

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }
    }
}
