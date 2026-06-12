package com.digital.backend.exceptions;

public class AccountServiceFeignException extends RuntimeException{
    public AccountServiceFeignException(String message){
        super(message);
    }
}