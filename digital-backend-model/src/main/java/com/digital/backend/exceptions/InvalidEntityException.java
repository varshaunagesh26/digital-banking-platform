package com.digital.backend.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_CONTENT)
public class InvalidEntityException extends RuntimeException {

    private final String entityName;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ValidationError> errors = new ArrayList<>();

    public InvalidEntityException(String message){
        super(message);
        entityName = " ";
    }

    public InvalidEntityException(String message, Throwable cause){
        super(message, cause);
        entityName = " ";
    }

    public InvalidEntityException(String message, String entityName){
        super(message);
        this.entityName = entityName;
    }

    public void addValidationError(String field, String value, String message){
        errors.add(new ValidationError(field,value,message));
    }

}
