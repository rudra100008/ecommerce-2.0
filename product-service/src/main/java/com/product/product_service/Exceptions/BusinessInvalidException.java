package com.product.product_service.Exceptions;

public class BusinessInvalidException extends RuntimeException{
    public BusinessInvalidException(String message){
        super(message);
    }

    public BusinessInvalidException(String message,Throwable cause){
        super(message, cause);
    }
}
