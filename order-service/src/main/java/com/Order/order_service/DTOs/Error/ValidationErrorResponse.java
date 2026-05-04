package com.Order.order_service.DTOs.Error;

public record ValidationErrorResponse(
        String field,
        String message
) {

}
