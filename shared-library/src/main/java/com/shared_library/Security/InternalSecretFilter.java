package com.shared_library.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class InternalSecretFilter extends OncePerRequestFilter {

    @Value("${internal.secret-key}")
    private String internalSecret;

    private static final List<String >PUBLIC_PATHS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/refresh",
            "/oauth2/",
            "/login/oauth2/"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if(isPath(path)){
            filterChain.doFilter(request,response);
            return;
        }

        String secret  = request.getHeader("X-Internal-Secret");

        log.info("internal-secret= {},secret ={}",internalSecret,secret);
        if(secret == null || secret.isEmpty() || !internalSecret.equals(secret)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    "{ \"message\": \"Direct access not allowed\" }"
            );
            return;
        }

        filterChain.doFilter(request,response);


    }

    private boolean isPath(String path){
       return PUBLIC_PATHS.stream()
                .anyMatch(path::startsWith);
    }


}
