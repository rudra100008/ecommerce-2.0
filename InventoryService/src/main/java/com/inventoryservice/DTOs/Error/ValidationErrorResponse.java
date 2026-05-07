package com.inventoryservice.DTOs.Error;

public record ValidationErrorResponse(
        String field,
        String message
) {

}
