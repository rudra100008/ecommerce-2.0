package com.user.user_service.Security;

import com.user.user_service.Entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@ConfigurationProperties(prefix = "jwt")
@Component
@Getter
@Setter
public class JwtUtils {
    private String secret;

    // maps to jwt.expires
    private Long expires;

    // maps to jwt.refresh-expiration-ms
    private Long refreshExpirationMs;


    public String generateToken(User user){
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId",user.getId())
                .claim("role",user.getRole().name())
                .claim("provider",user.getProvider().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expires))
                .signWith(getSigningKey())
                .compact();
    }

    public String refreshToken(User user){
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("type","refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
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
        return validateToken(token).getExpiration().before(new Date());
    }
    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }




}
