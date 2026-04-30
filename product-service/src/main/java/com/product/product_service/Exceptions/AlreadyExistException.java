package com.product.product_service.Exceptions;

public class AlreadyExistException extends RuntimeException{

    public AlreadyExistException(String message){
        super(message);
    }

    public AlreadyExistException(String message,Throwable cause){
        super(message,cause);
    }
}
