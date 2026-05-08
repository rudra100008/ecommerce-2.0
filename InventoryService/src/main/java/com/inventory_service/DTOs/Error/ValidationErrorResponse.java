package com.inventory_service.DTOs.Error;

public record ValidationErrorResponse(
        String field,
        String message
) {

}
