package com.inventory_service.Exceptions;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalException {


    private ResponseEntity<?> errorResponse(HttpStatus status, String message, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("timeStamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("Error", status.getReasonPhrase());
        response.put("message", message);
        response.put("path", request.getDescription(false));

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e, WebRequest request) {
        log.error("Unexpected error",e);
        return errorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexcepted error occurred: " + e.getMessage(),
                request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                request);
    }

    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<?> handleIllegalAccessException(IllegalAccessException e, WebRequest request) {
        return errorResponse(HttpStatus.UNAUTHORIZED, e.getMessage(), request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException e, WebRequest request) {
        return errorResponse(HttpStatus.UNAUTHORIZED, e.getMessage(), request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e, WebRequest request) {
        return errorResponse(
                HttpStatus.NOT_FOUND,
                e.getMessage(),
                request);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException e, WebRequest request) {
        return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    @ExceptionHandler(ReservationInvalidException.class)
    public ResponseEntity<?> handleReservationInvalidException(ReservationInvalidException e,WebRequest request){
        return errorResponse(HttpStatus.CONFLICT,e.getMessage(),request);
    }

    // GlobalExceptionHandler
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrity(DataIntegrityViolationException e) {
        String message = e.getMessage() != null && e.getMessage().contains("sku")
                ? "A product with a similar name already exists. Please use a custom SKU."
                : "Data conflict error. Please try again.";
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", message));
    }



}
