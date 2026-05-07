package com.Order.orderservice.DTOs.Error;

public record ValidationErrorResponse(
        String field,
        String message
) {

}
