package com.promo.otp.security;

import com.promo.otp.model.Role;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);
    private static final Map<String, List<Role>> SECURED_PATHS = new HashMap<>();
    
    static {
        // Публичные endpoints (не требуют аутентификации)
        SECURED_PATHS.put("/api/auth/register", new ArrayList<>());
        SECURED_PATHS.put("/api/auth/login", new ArrayList<>());
        SECURED_PATHS.put("/api/health", new ArrayList<>());
        
        // Защищённые endpoints (требуют любой роли)
        List<Role> anyRole = List.of(Role.USER, Role.ADMIN);
        SECURED_PATHS.put("/api/otp/generate", anyRole);
        SECURED_PATHS.put("/api/otp/validate", anyRole);
        
        // Админ endpoints
        List<Role> adminOnly = List.of(Role.ADMIN);
        SECURED_PATHS.put("/api/admin/config", adminOnly);
        SECURED_PATHS.put("/api/admin/users", adminOnly);
    }
    
    public static boolean authenticate(HttpExchange exchange, HttpHandler next) throws IOException {
        String path = exchange.getRequestURI().getPath();
        List<Role> requiredRoles = getRequiredRoles(path);
        
        // Публичный endpoint
        if (requiredRoles == null || requiredRoles.isEmpty()) {
            if (next != null) {
                next.handle(exchange);
            }
            return true;
        }
        
        // Получаем токен из заголовка
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(exchange, "Missing or invalid authorization header");
            return false;
        }
        
        String token = authHeader.substring(7);
        
        try {
            if (!JwtUtil.isTokenValid(token)) {
                sendUnauthorized(exchange, "Invalid or expired token");
                return false;
            }
            
            Role userRole = JwtUtil.getRoleFromToken(token);
            
            if (!requiredRoles.contains(userRole)) {
                sendForbidden(exchange, "Insufficient permissions");
                return false;
            }
            
            // Сохраняем информацию о пользователе
            exchange.setAttribute("userId", JwtUtil.getUserIdFromToken(token));
            exchange.setAttribute("username", JwtUtil.getUsernameFromToken(token));
            exchange.setAttribute("role", userRole);
            
            if (next != null) {
                next.handle(exchange);
            }
            return true;
            
        } catch (Exception e) {
            logger.error("Authentication error: {}", e.getMessage());
            sendUnauthorized(exchange, "Authentication failed: " + e.getMessage());
            return false;
        }
    }
    
    private static List<Role> getRequiredRoles(String path) {
        // Точное совпадение
        if (SECURED_PATHS.containsKey(path)) {
            return SECURED_PATHS.get(path);
        }
        
        // Проверка префиксов
        if (path.startsWith("/api/admin/")) {
            return SECURED_PATHS.get("/api/admin/users");
        }
        if (path.startsWith("/api/otp/")) {
            return SECURED_PATHS.get("/api/otp/generate");
        }
        
        // По умолчанию - защищённые endpoints
        if (path.startsWith("/api/")) {
            return List.of(Role.USER, Role.ADMIN);
        }
        
        return null;
    }
    
    private static void sendUnauthorized(HttpExchange exchange, String message) throws IOException {
        String response = String.format("{\"success\": false, \"error\": \"%s\"}", message);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(401, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }
    
    private static void sendForbidden(HttpExchange exchange, String message) throws IOException {
        String response = String.format("{\"success\": false, \"error\": \"%s\"}", message);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(403, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }
}
