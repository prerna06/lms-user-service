package com.tekcapzule.lms.user.domain.exception;

public class FoundException extends RuntimeException{
    public FoundException(String errorMessage, Throwable throwable){
        super(errorMessage, throwable);
    }

    public FoundException(String errorMessage){
        super(errorMessage);
    }
}
