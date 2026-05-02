package com.promo.otp.security;

import com.promo.otp.config.AppConfig;
import com.promo.otp.model.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private static final SecretKey SECRET_KEY;
    private static final long EXPIRATION_MS;

    static {
        String secret = AppConfig.get("jwt.secret", "default-secret-key-for-jwt-otp-service-2024");
        SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        EXPIRATION_MS = AppConfig.getLong("jwt.expiration.ms", 86400000L);
    }

    public static String generateToken(String username, Long userId, Role role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token expired: {}", e.getMessage());
            throw new RuntimeException("Token expired");
        } catch (JwtException e) {
            logger.warn("Invalid JWT token: {}", e.getMessage());
            throw new RuntimeException("Invalid token");
        }
    }

    public static String getUsernameFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.getSubject();
    }

    public static Long getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("userId", Long.class);
    }

    public static Role getRoleFromToken(String token) {
        Claims claims = validateToken(token);
        String roleStr = claims.get("role", String.class);
        return Role.fromString(roleStr);
    }

    public static boolean isTokenValid(String token) {
        try {
            validateToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
