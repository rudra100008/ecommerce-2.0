package com.user.user_service.SecurityConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared_library.Error.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuthExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String message = "Authentication failed";

        if(authException instanceof BadCredentialsException){
            message = "Email or password is incorrect";
        }

        if(authException instanceof DisabledException){
            message = "Your account is disabled";
        }

        if (authException instanceof OAuth2AuthenticationException){
            message = "OAuth2 authentication failed";
        }

        buildErrorResponse(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                "UNAUTHORIZED",
                message,
                request.getServletPath()
        );
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        buildErrorResponse(
                response,
                HttpServletResponse.SC_FORBIDDEN,
                "FORBIDDEN",
                "You do not have permission to access this resource",
                request.getServletPath()
        );
    }


    private void buildErrorResponse(
            HttpServletResponse response,
            int status,
            String error,
            String message,
            String path
    ) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status);

        objectMapper.writeValue(
                response.getOutputStream(),
                new ApiErrorResponse(
                        LocalDateTime.now(),
                        status,
                        error,
                        message,
                        path
                )
        );
    }
}
