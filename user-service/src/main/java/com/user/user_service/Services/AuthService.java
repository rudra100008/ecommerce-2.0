package com.user.user_service.Services;

import com.user.user_service.DTOs.AuthDTO.AuthResponse;
import com.user.user_service.DTOs.AuthDTO.LoginRequest;
import com.user.user_service.DTOs.AuthDTO.RegisterRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(String refreshToken);
}
