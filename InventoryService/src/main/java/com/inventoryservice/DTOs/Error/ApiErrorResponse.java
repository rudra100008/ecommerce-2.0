package com.inventoryservice.DTOs.Error;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        LocalDateTime timeStamp,
        int status,
        String error,
        String message,
        String path
) {
}
