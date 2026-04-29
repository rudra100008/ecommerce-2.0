package com.product.product_service.DTOs;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        LocalDateTime timeStamp,
        HttpStatus status,
        String error,
        String message,
        String path
) {
}
