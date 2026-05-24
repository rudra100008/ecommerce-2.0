package com.user.user_service.Services;

import com.user.user_service.DTOs.AuthDTO.AuthResponse;
import com.user.user_service.DTOs.AuthDTO.LoginRequest;
import com.user.user_service.DTOs.AuthDTO.RegisterRequest;
import com.user.user_service.Entities.CustomUserPrincipal;
import com.user.user_service.Entities.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(CustomUserPrincipal userPrincipal);

    AuthResponse refresh(String refreshToken);
}
