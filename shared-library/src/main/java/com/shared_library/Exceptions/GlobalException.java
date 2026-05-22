package com.shared_library.Exceptions;


import com.shared_library.Error.ApiErrorResponse;
import com.shared_library.Error.ValidationErrorResponse;
import com.shared_library.Error.ValidationErrors;
import feign.Response;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

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
                "Something went wrong" ,
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
                HttpStatus.CONFLICT,
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

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<?> handleInsufficientStockException(InsufficientStockException e, WebRequest request) {
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                request);
    }

    @ExceptionHandler(MediaUploadException.class)
    public ResponseEntity<?> handleMediaUploadException(MediaUploadException e, WebRequest request) {
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                request);
    }
    @ExceptionHandler(ServiceNotFoundException.class)
    public ResponseEntity<?> handleServiceNotFoundException(ServiceNotFoundException e, WebRequest request) {
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                request);
    }
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleRateLimit(
            RateLimitExceededException ex,WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS) // 429
                .header("Retry-After", "1")           // tell client to retry after 1 second
                .body(new ApiErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.TOO_MANY_REQUESTS.value(),
                        HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
                        ex.getMessage(),
                        request.getDescription(false)
                ));
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ApiErrorResponse> handleRequestNotPermitted(
            RequestNotPermitted ex,WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", "1")
                .body(new ApiErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.TOO_MANY_REQUESTS.value(),
                        HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
                        "Too many requests.Please try again later.",
                        request.getDescription(false)
                ));
    }


    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException e, WebRequest request) {
        return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,WebRequest request)
    {
        List<ValidationErrorResponse> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(f -> new ValidationErrorResponse(f.getField(),f.getDefaultMessage()))
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrors(errors));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e,WebRequest request){
        return errorResponse(
                HttpStatus.UNAUTHORIZED,
                e.getMessage(),
                request
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException e, WebRequest request) {
        return errorResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password",
                request
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFound(UsernameNotFoundException e, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(
                LocalDateTime.now(), 404, "Not Found", "Email not found",
                request.getDescription(false)
        ));
    }
//    // GlobalExceptionHandler
//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<?> handleDataIntegrity(DataIntegrityViolation e,WebRequest request) {
//        String message = e.getMessage() != null && e.getMessage().contains("sku")
//                ? "A product with a similar name already exists. Please use a custom SKU."
//                : "Data conflict error. Please try again.";
//        return errorResponse(HttpStatus.CONFLICT,message,request);
//    }



}
