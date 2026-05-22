package com.gateway.ApiGateway.Security;

import com.shared_library.Utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter implements GlobalFilter, Ordered {
    private final JwtUtils jwtUtils;
    private final AntPathMatcher antPathMatcher;
    @Value("${internal.secret-key}")
    private String  internalSecretKey;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/**",
            "/oauth2/**",
            "/login/oauth2/**",
            "/api/products/fetchAll"
    );

    private static final List<String> ADMIN_PATHS = List.of(
            "/api/admin/**",
            "/api/products/admin/**"
    );

    private static final List<String> ADMIN_ROLES = List.of(
            "ROLE_ADMIN",
            "ROLE_SUB_ADMIN"
    );


    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        if(isPublic(path)){
            return chain.filter(exchange);
        }

       String token = extractToken(exchange);
        if(token == null){
            return buildErrorResponse(exchange,HttpStatus.UNAUTHORIZED,"Missing token");
        }
        try{
            Claims claims = jwtUtils.validateToken(token);
            String role = jwtUtils.extractRole(token);
            if(isAdminPath(path) && !isAdmin(role)){
                return buildErrorResponse(
                        exchange,
                        HttpStatus.UNAUTHORIZED,
                        "Access Denied. Admin privileges required."
                );
            }

            ServerHttpRequest modified = exchange.getRequest()
                    .mutate()
                    .headers(headers -> {
                        headers.remove("X-User-Id");
                        headers.remove("X-User-Role");
                        headers.remove("X-User-Email");
                        headers.remove("X-Internal-Secret");
                        headers.remove(HttpHeaders.AUTHORIZATION);
                    })
                    .header("X-User-Id",String.valueOf(claims.get("userId")))
                    .header("X-User-Role",String.valueOf(claims.get("role")))
                    .header("X-User-Email",claims.getSubject())
                    .header("X-Internal-Secret",internalSecretKey)
                    .build();

            return chain.filter(
                    exchange.mutate().request(modified).build()
            );
        }catch (ExpiredJwtException e){
            log.error("Token expired:{}",e.getMessage());
            return buildErrorResponse(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    "Token expired"
            );
        }catch (Exception e){
            log.error("Token validation failed:{}",e.getMessage(),e);
            return buildErrorResponse(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    "Invalid token"
            );
        }
    }


    private boolean isPublic(String path) {
        return PUBLIC_PATHS.stream()
                .anyMatch(p -> antPathMatcher.match(p,path));
    }
    private boolean isAdminPath(String path){
        return ADMIN_PATHS.stream()
                .anyMatch(a -> antPathMatcher.match(a,path));
    }
    private boolean isAdmin(String role){
        return ADMIN_ROLES.contains(role);
    }
    private String extractToken(ServerWebExchange exchange){
        HttpCookie accessCookie  = exchange.getRequest().getCookies().getFirst("accessToken");
        if(accessCookie != null && !accessCookie.getValue().isEmpty()){
            log.info("Token found in cookies");
            return accessCookie.getValue();
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(org.springframework.http.HttpHeaders.AUTHORIZATION);
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            log.info("Token found in Authorization header");
            return authHeader.substring(7);
        }
        return null;
    }
    private Mono<Void> buildErrorResponse(
            ServerWebExchange exchange,
            HttpStatus status,
            String message
    ){

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = ("{\"message\": \""+message+"\"}").getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
