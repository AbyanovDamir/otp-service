package com.promo.otp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class TelegramService {
    private static final Logger logger = LoggerFactory.getLogger(TelegramService.class);
    private final String botToken;
    private final String apiUrl;

    public TelegramService() {
        Properties config = loadConfig();
        this.botToken = config.getProperty("telegram.bot.token", "");
        this.apiUrl = config.getProperty("telegram.api.url", "https://api.telegram.org/bot");
        logger.info("Telegram notification service initialized");
        logger.info("Telegram API URL: {}", this.apiUrl);
        logger.info("Telegram Bot Token: {}", this.botToken != null ? "configured" : "missing");
    }

    private Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("telegram.properties")) {
            if (input != null) {
                props.load(input);
                logger.info("Loaded Telegram configuration");
            } else {
                logger.warn("telegram.properties not found, using defaults");
            }
        } catch (IOException e) {
            logger.error("Failed to load Telegram configuration", e);
        }
        return props;
    }

    public boolean send(String chatId, String code) {
        if (botToken == null || botToken.isEmpty()) {
            logger.error("Telegram bot token not configured");
            return false;
        }

        if (chatId == null || chatId.isEmpty()) {
            logger.error("No chat ID provided");
            return false;
        }

        String message = String.format(
            "🔐 *OTP Verification Code*\n\nYour verification code is: `%s`\n\nThis code will expire in 5 minutes.",
            code
        );

        // ИСПРАВЛЕНО: добавляем "bot" перед токеном и используем POST вместо GET
        String url = String.format("%sbot%s/sendMessage", apiUrl, botToken);
        
        // Используем POST запрос с JSON телом
        String jsonBody = String.format("{\"chat_id\": \"%s\", \"text\": \"%s\", \"parse_mode\": \"Markdown\"}",
                chatId, escapeJson(message));

        logger.debug("Sending Telegram request to: {}", url);
        
        return sendTelegramRequestPost(url, jsonBody);
    }

    private boolean sendTelegramRequestPost(String url, String jsonBody) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode == 200) {
                logger.info("Telegram message sent successfully");
                return true;
            } else {
                logger.error("Telegram API error. Status: {}, Response: {}", statusCode, response.body());
                return false;
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted while sending Telegram message: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        } catch (IOException e) {
            logger.error("IO error sending Telegram message: {}", e.getMessage());
            return false;
        }
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
    
    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }
}
