package com.media_service.Exceptions;

import com.media_service.DTO.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalException {


    private ResponseEntity<?> errorResponse( HttpStatus status,String message, WebRequest request){
        return ResponseEntity.status(status).body(new ApiErrorResponse(
                LocalDateTime.now(),
                status,
                status.getReasonPhrase(),
                message,
                request.getDescription(false)
        ));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e, WebRequest request){
        return  errorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexcepted error occurred:" + e.getMessage(),
                request
        );
    }

    @ExceptionHandler(MediaUploadException.class)
    public ResponseEntity<?> mediaUploadException(MediaUploadException e, WebRequest request){
        return  errorResponse(
                HttpStatus.CONFLICT,
                e.getMessage(),
                request
        );
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> ioException(IOException e, WebRequest request){
        return  errorResponse(
                HttpStatus.CONFLICT,
                e.getMessage(),
                request
        );
    }
}
