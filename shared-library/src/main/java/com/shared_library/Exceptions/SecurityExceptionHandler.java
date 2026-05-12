// NEW file in shared-library:
// com/shared_library/Exceptions/SecurityExceptionHandler.java

package com.shared_library.Exceptions;

import com.shared_library.Error.ApiErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;

@RestControllerAdvice
@RequiredArgsConstructor
public class SecurityExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException e, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiErrorResponse(
                LocalDateTime.now(), 401, "Unauthorized", "Invalid credentials",
                request.getDescription(false)
        ));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFound(UsernameNotFoundException e, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(
                LocalDateTime.now(), 404, "Not Found", "Email not found",
                request.getDescription(false)
        ));
    }
}