package com.inventory_service.Exceptions;

public class ReservationInvalidException extends RuntimeException{
    public ReservationInvalidException(String message){
        super(message);
    }
    public ReservationInvalidException(String message,Throwable cause){
        super(message,cause);
    }
}
