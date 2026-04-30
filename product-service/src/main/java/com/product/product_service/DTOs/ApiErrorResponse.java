package com.product.product_service.DTOs;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        LocalDateTime timeStamp,
        int status,
        String error,
        String message,
        String path
) {
}
