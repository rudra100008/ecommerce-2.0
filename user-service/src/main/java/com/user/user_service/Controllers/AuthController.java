package com.user.user_service.Controllers;

import com.user.user_service.DTOs.AuthDTO.AuthResponse;
import com.user.user_service.DTOs.AuthDTO.LoginRequest;
import com.user.user_service.DTOs.AuthDTO.RegisterRequest;
import com.user.user_service.Entities.User;
import com.user.user_service.Services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @PostMapping(value = "/register",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> register(
            HttpServletResponse response,
            @RequestPart("user") RegisterRequest registerRequest,
            @RequestPart("image") MultipartFile imageFile
    ){
        AuthResponse authResponse = this.authService.register(registerRequest,imageFile);

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

        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );
        User user = (User) authentication.getPrincipal();

        AuthResponse authResponse = this.authService.login(user);
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
