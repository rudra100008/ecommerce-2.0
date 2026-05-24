package com.user.user_service.Controllers;

import com.user.user_service.DTOs.AuthDTO.AuthResponse;
import com.user.user_service.DTOs.AuthDTO.LoginRequest;
import com.user.user_service.DTOs.AuthDTO.RegisterRequest;
import com.user.user_service.Entities.CustomUserPrincipal;
import com.user.user_service.Entities.User;
import com.user.user_service.Services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @PostMapping(value = "/register")
    public ResponseEntity<?> register(
            HttpServletResponse response,
            @RequestBody RegisterRequest registerRequest
    ){
        AuthResponse authResponse = this.authService.register(registerRequest);

        addAuthCookies(response,authResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            HttpServletResponse response,
            @Valid @RequestBody LoginRequest loginRequest
    ){

        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );
        CustomUserPrincipal user = (CustomUserPrincipal) authentication.getPrincipal();

        AuthResponse authResponse = this.authService.login(user);

        addAuthCookies(response,authResponse);
        return ResponseEntity.status(HttpStatus.OK).body(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            HttpServletResponse response,
            @CookieValue(value = "refreshToken",required = false) String cookieToken,
            @RequestParam(value = "refreshToken",required = false)String paramToken
    ){
        String refreshToken = cookieToken != null
                ? cookieToken
                :paramToken;
        if(refreshToken == null){
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message","Refresh token missing."
                    ));
        }
        AuthResponse authResponse = authService.refresh(refreshToken);

        addAuthCookies(response,authResponse);
        return ResponseEntity.ok(authResponse);
    }

    private void addAuthCookies(HttpServletResponse response, AuthResponse authResponse) {
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", authResponse.accessToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", authResponse.refreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();

        ResponseCookie roleCookie = ResponseCookie.from("role", authResponse.role())
                .httpOnly(false)          // middleware must read this
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, roleCookie.toString());
    }
}
