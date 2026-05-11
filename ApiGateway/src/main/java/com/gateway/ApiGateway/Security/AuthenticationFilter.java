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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter implements GlobalFilter, Ordered {
    private final JwtUtils jwtUtils;
    @Value("${internal.secret-key}")
    private final String  internalSecretKey;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/",
            "/oauth2/",
            "/login/oauth2/"
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

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return buildErrorResponse(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    "Missing token"
            );
        }
        try{
            Claims claims = jwtUtils.validateToken(authHeader.substring(7));

            ServerHttpRequest modified = exchange.getRequest()
                    .mutate()
                    .header("X-User-Id",claims.get("userId").toString())
                    .header("X-User-Role",claims.get("role").toString())
                    .header("X-User-Email",claims.getSubject())
                    .header("X-Internal-Secret",internalSecretKey)
                    .headers(h -> h.remove(HttpHeaders.AUTHORIZATION))
                    .build();

            return chain.filter(
                    exchange.mutate().request(modified).build()
            );
        }catch (ExpiredJwtException e){
            return buildErrorResponse(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    "Token expired"
            );
        }catch (Exception e){
            return buildErrorResponse(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    "Invalid token"
            );
        }
    }


    private boolean isPublic(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
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
