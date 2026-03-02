package com.db.account.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.BAD_REQUEST
)
public class EntityAlreadyDeletedException extends RuntimeException{
    public EntityAlreadyDeletedException(String message){
        super(message);
    }
}
