package com.Order.orderservice.DTOs.Error;

import java.util.List;

public record ValidationErrors(
        List<ValidationErrorResponse> errors
) {
}
