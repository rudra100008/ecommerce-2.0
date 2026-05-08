package com.inventory_service.DTOs.Error;

import java.util.List;

public record ValidationErrors(
        List<ValidationErrorResponse> errors
) {
}
