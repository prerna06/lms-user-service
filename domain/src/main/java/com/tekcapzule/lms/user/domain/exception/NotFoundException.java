package com.tekcapzule.lms.user.domain.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String errorMessage, Throwable throwable){
        super(errorMessage, throwable);
    }

    public NotFoundException(String errorMessage){
        super(errorMessage);
    }
}
