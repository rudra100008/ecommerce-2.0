package com.user.user_service.Config;

import com.user.user_service.SecurityConfig.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailService userDetailService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final AuthExceptionHandler authExceptionHandler;
    private final CustomLogoutSuccessHandler logoutSuccessHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration){
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider dao = new DaoAuthenticationProvider(userDetailService);
        dao.setPasswordEncoder(passwordEncoder());
        return dao;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/auth/oauth2/**").permitAll()
                        .requestMatchers("/oauth2/**","/login/oauth2/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(authExceptionHandler)
                        .authenticationEntryPoint(authExceptionHandler))
                .oauth2Login(oauth2 -> oauth2
                        .redirectionEndpoint(endpoint -> endpoint
                                .baseUri("/api/auth/oauth2/callback/*"))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler))
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .deleteCookies("accessToken", "refreshToken")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .permitAll())
                .build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of("http://localhost:3000"));
        cors.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));
        cors.setExposedHeaders(List.of("Set-Cookie","Authorization","X-XSRF-TOKEN"));
        cors.setAllowCredentials(true); // for cookies

        UrlBasedCorsConfigurationSource url  = new UrlBasedCorsConfigurationSource();
        url.registerCorsConfiguration("/**",cors);
        return url;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
