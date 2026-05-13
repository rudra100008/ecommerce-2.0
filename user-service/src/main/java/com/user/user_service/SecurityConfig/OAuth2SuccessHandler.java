package com.user.user_service.SecurityConfig;

import com.shared_library.Utils.JwtUtils;
import com.user.user_service.Entities.User;
import com.user.user_service.Enums.AuthProvider;
import com.user.user_service.Enums.RoleStatus;
import com.user.user_service.Repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    @Value("${app.oauth2.redirect-uri}")
    private String frontendRedirectUri;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String imageUrl = oAuth2User.getAttribute("picture");
        String googleId = oAuth2User.getAttribute("sub");


        User user = this.userRepository.findByEmail(email)
                .map(existingUser -> updateExisitingUser(existingUser, imageUrl))
                .orElseGet(() -> createGoogleUser(email, name, imageUrl, googleId));

        String accessToken = jwtUtils.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.getProvider().name()
        );
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());


        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false)// for deployment
                .sameSite("Lax")
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)// for deployment
                .sameSite("Lax")
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE,refreshCookie.toString());

        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendRedirectUri)
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }


    private User createGoogleUser(
            String email, String name,
            String imageUrl, String googleId
    ) {

        String base = email.split("@")[0];
        String username = userRepository.existsByUsername(base)
                ? base + "_" + UUID.randomUUID().toString().substring(0,8)
                : base;
        User newUser = User.builder()
                .email(email)
                .fullName(name)
                .username(username)
                .imageUrl(imageUrl)
                .providerId(googleId)
                .provider(AuthProvider.GOOGLE)
                .imageCustomized(false)
                .role(RoleStatus.ROLE_CUSTOMER)
                .active(true)
                .build();

        return this.userRepository.save(newUser);
    }

    private User updateExisitingUser(User user, String imageUrl) {
        if (!Boolean.TRUE.equals(user.getImageCustomized())) {
            user.setImageUrl(imageUrl);
        }

        return userRepository.save(user);
    }
}
