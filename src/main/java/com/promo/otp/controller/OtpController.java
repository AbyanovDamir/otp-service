package com.promo.otp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promo.otp.model.ApiResponse;
import com.promo.otp.service.OtpService;
import com.promo.otp.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class OtpController {
    private static final Logger logger = LoggerFactory.getLogger(OtpController.class);
    private final OtpService otpService;
    private final ObjectMapper objectMapper;

    public OtpController() {
        this.otpService = new OtpService();
        this.objectMapper = JsonUtil.getObjectMapper();
    }

    public void generate(HttpExchange exchange) throws IOException {
        Long userId = (Long) exchange.getAttribute("userId");
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        try {
            Map<String, String> request = objectMapper.readValue(body, Map.class);
            String operationId = request.get("operationId");
            String channel = request.getOrDefault("channel", "file");

            if (operationId == null || operationId.isEmpty()) {
                sendResponse(exchange, 400, ApiResponse.error("operationId is required"));
                return;
            }

            Map<String, Object> result = otpService.generate(userId, operationId, channel);
            sendResponse(exchange, 200, ApiResponse.success("OTP generated successfully", result));
            logger.info("POST /api/otp/generate - User {} generated OTP for operation {}", userId, operationId);

        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, ApiResponse.error(e.getMessage()));
            logger.warn("OTP generation failed: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("OTP generation error", e);
            sendResponse(exchange, 500, ApiResponse.error("Internal server error"));
        }
    }

    public void validate(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        try {
            Map<String, String> request = objectMapper.readValue(body, Map.class);
            String operationId = request.get("operationId");
            String code = request.get("code");

            if (operationId == null || code == null) {
                sendResponse(exchange, 400, ApiResponse.error("operationId and code are required"));
                return;
            }

            Map<String, Object> result = otpService.validate(operationId, code);
            sendResponse(exchange, 200, ApiResponse.success("OTP validated successfully", result));
            logger.info("POST /api/otp/validate - Validated OTP for operation {}", operationId);

        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, ApiResponse.error(e.getMessage()));
            logger.warn("OTP validation failed: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("OTP validation error", e);
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
