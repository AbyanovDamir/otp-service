package com.promo.otp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promo.otp.model.ApiResponse;
import com.promo.otp.service.AuthService;
import com.promo.otp.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    public AuthController() {
        this.authService = new AuthService();
        this.objectMapper = JsonUtil.getObjectMapper();
    }

    public void register(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        try {
            Map<String, String> request = objectMapper.readValue(body, Map.class);
            String username = request.get("username");
            String password = request.get("password");
            String email = request.get("email");
            String phone = request.get("phone");

            if (username == null || password == null || email == null) {
                sendResponse(exchange, 400, ApiResponse.error("Username, password and email are required"));
                return;
            }

            Map<String, Object> result = authService.register(username, password, email, phone);
            sendResponse(exchange, 201, ApiResponse.success("User registered successfully", result));
            logger.info("POST /api/auth/register - User registered: {}", username);

        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, ApiResponse.error(e.getMessage()));
            logger.warn("Registration failed: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Registration error", e);
            sendResponse(exchange, 500, ApiResponse.error("Internal server error"));
        }
    }

    public void login(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        try {
            Map<String, String> request = objectMapper.readValue(body, Map.class);
            String username = request.get("username");
            String password = request.get("password");

            if (username == null || password == null) {
                sendResponse(exchange, 400, ApiResponse.error("Username and password are required"));
                return;
            }

            Map<String, Object> result = authService.login(username, password);
            sendResponse(exchange, 200, ApiResponse.success("Login successful", result));
            logger.info("POST /api/auth/login - User logged in: {}", username);

        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 401, ApiResponse.error(e.getMessage()));
            logger.warn("Login failed: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Login error", e);
            sendResponse(exchange, 500, ApiResponse.error("Internal server error"));
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, ApiResponse<?> response) throws IOException {
        String json = objectMapper.writeValueAsString(response);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, json.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(json.getBytes());
        }
    }
}
