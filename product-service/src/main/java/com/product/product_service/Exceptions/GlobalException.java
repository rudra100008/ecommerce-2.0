package com.product.product_service.Exceptions;

import com.product.product_service.DTOs.ApiErrorResponse;
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

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalException {


    private ResponseEntity<?> errorResponse(HttpStatus status, String message, WebRequest request) {
        return ResponseEntity.status(status).body(new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getDescription(false)
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e, WebRequest request) {
        log.error("Unexpected error",e);
        return errorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred: " + e.getMessage(),
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

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<?> handleAlreadyExistException(AlreadyExistException e, WebRequest request) {
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                request);
    }

    @ExceptionHandler(ImageInvalidException.class)
    public ResponseEntity<?> handleImageInvalidException(ImageInvalidException e, WebRequest request) {
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                request);
    }

    @ExceptionHandler(BusinessInvalidException.class)
    public ResponseEntity<?> handleBusinessInvalidException(BusinessInvalidException e, WebRequest request) {
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                request);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException e, WebRequest request) {
        return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }



    // GlobalExceptionHandler
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrity(DataIntegrityViolationException e,WebRequest request) {
        String message = e.getMessage() != null && e.getMessage().contains("sku")
                ? "A product with a similar name already exists. Please use a custom SKU."
                : "Data conflict error. Please try again.";
        return errorResponse(HttpStatus.CONFLICT,message,request);
    }



}
