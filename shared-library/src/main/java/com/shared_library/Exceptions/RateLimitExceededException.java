package com.shared_library.Exceptions;

public class RateLimitExceededException extends RuntimeException{
    public RateLimitExceededException(String message){
        super(message);
    }

    public RateLimitExceededException(String message,Throwable cause){
        super(message, cause);
    }
}
