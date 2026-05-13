package com.shared_library.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
@Getter
@Setter
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private Long expirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private Long refreshExpirationMs;



    public String generateToken(Long userId, String email, String role,
                                String provider) {
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("role", role)
                .claim("provider", provider)
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis() + refreshExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }


    public Claims validateToken(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaims(String token, Function<Claims,T> resolver){
        Claims claims = validateToken(token);
        return resolver.apply(claims);
    }


    public String extractEmail(String token){
        return extractClaims(token,Claims::getSubject);
    }

    public Long extractUserId(String token){
        return extractClaims(token,
                claims -> claims.get("userId",Long.class));
    }

    public String extractRole(String token){
        return extractClaims(token,
                claims -> claims.get("role",String.class));
    }

    public boolean isTokenValid(String token){
        return validateToken(token).getExpiration().after(new Date());
    }
    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }




}
