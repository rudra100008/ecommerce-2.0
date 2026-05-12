package com.user.user_service.Controllers;

import com.user.user_service.DTOs.AuthDTO.AuthResponse;
import com.user.user_service.DTOs.AuthDTO.LoginRequest;
import com.user.user_service.DTOs.AuthDTO.RegisterRequest;
import com.user.user_service.Services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            HttpServletResponse response,
            @Valid @RequestBody RegisterRequest registerRequest
    ){
        AuthResponse authResponse = this.authService.register(registerRequest);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", authResponse.accessToken())
                .httpOnly(true)
                .secure(false)// for deployment
                .sameSite("Lax")
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken",authResponse.refreshToken())
                .httpOnly(true)
                .secure(false)// for deployment
                .sameSite("Lax")
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE,refreshCookie.toString());

        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            HttpServletResponse response,
            @Valid @RequestBody LoginRequest loginRequest
    ){

        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );
        AuthResponse authResponse = this.authService.login(loginRequest);
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", authResponse.accessToken())
                .httpOnly(true)
                .secure(false)// for deployment
                .sameSite("Lax")
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken",authResponse.refreshToken())
                .httpOnly(true)
                .secure(false)// for deployment
                .sameSite("Lax")
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE,refreshCookie.toString());

        return ResponseEntity.status(HttpStatus.OK).body(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue("refreshToken") String refreshToken) {
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }
}
