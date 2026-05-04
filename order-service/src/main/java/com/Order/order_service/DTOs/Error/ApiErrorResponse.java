package com.Order.order_service.DTOs.Error;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        LocalDateTime timeStamp,
        int status,
        String error,
        String message,
        String path
) {
}
