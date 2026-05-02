package com.promo.otp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promo.otp.dao.OtpCodeDAO;
import com.promo.otp.dao.UserDAO;
import com.promo.otp.model.ApiResponse;
import com.promo.otp.model.OtpConfig;
import com.promo.otp.model.User;
import com.promo.otp.service.OtpService;
import com.promo.otp.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final OtpService otpService;
    private final UserDAO userDAO;
    private final OtpCodeDAO otpCodeDAO;
    private final ObjectMapper objectMapper;

    public AdminController() {
        this.otpService = new OtpService();
        this.userDAO = new UserDAO();
        this.otpCodeDAO = new OtpCodeDAO();
        this.objectMapper = JsonUtil.getObjectMapper();
    }

    public void getConfig(HttpExchange exchange) throws IOException {
        try {
            OtpConfig config = otpService.getConfig();
            sendResponse(exchange, 200, ApiResponse.success("Config retrieved", config));
            logger.info("GET /api/admin/config - Config retrieved");
        } catch (SQLException e) {
            logger.error("Failed to get config", e);
            sendResponse(exchange, 500, ApiResponse.error("Failed to retrieve config"));
        }
    }

    public void updateConfig(HttpExchange exchange) throws IOException {
        String username = (String) exchange.getAttribute("username");
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        try {
            Map<String, Integer> request = objectMapper.readValue(body, Map.class);
            Integer ttlSeconds = request.get("ttlSeconds");
            Integer codeLength = request.get("codeLength");

            if (ttlSeconds == null && codeLength == null) {
                sendResponse(exchange, 400, ApiResponse.error("At least one parameter is required"));
                return;
            }

            OtpConfig current = otpService.getConfig();
            int newTtl = ttlSeconds != null ? ttlSeconds : current.getTtlSeconds();
            int newLength = codeLength != null ? codeLength : current.getCodeLength();

            OtpConfig updated = otpService.updateConfig(newTtl, newLength, username);
            sendResponse(exchange, 200, ApiResponse.success("Config updated successfully", updated));
            logger.info("PUT /api/admin/config - Config updated by {}", username);

        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, ApiResponse.error(e.getMessage()));
        } catch (SQLException e) {
            logger.error("Failed to update config", e);
            sendResponse(exchange, 500, ApiResponse.error("Failed to update config"));
        }
    }

    public void getAllUsers(HttpExchange exchange) throws IOException {
        try {
            List<User> users = userDAO.findAllNonAdmin();
            sendResponse(exchange, 200, ApiResponse.success("Users retrieved", users));
            logger.info("GET /api/admin/users - Retrieved {} users", users.size());
        } catch (SQLException e) {
            logger.error("Failed to get users", e);
            sendResponse(exchange, 500, ApiResponse.error("Failed to retrieve users"));
        }
    }

    public void deleteUser(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String userIdStr = path.substring(path.lastIndexOf("/") + 1);

        try {
            Long userId = Long.parseLong(userIdStr);
            otpCodeDAO.deleteByUserId(userId);
            boolean deleted = userDAO.deleteById(userId);

            if (deleted) {
                sendResponse(exchange, 200, ApiResponse.success("User deleted successfully"));
                logger.info("DELETE /api/admin/users/{} - User deleted", userId);
            } else {
                sendResponse(exchange, 404, ApiResponse.error("User not found"));
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, ApiResponse.error("Invalid user ID"));
        } catch (SQLException e) {
            logger.error("Failed to delete user", e);
            sendResponse(exchange, 500, ApiResponse.error("Failed to delete user"));
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
