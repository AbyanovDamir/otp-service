package com.promo.otp.service;

import com.promo.otp.dao.OtpCodeDAO;
import com.promo.otp.dao.OtpConfigDAO;
import com.promo.otp.dao.UserDAO;
import com.promo.otp.model.OtpCode;
import com.promo.otp.model.OtpConfig;
import com.promo.otp.model.OtpStatus;
import com.promo.otp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OtpService {
    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    private final OtpCodeDAO otpCodeDAO;
    private final OtpConfigDAO otpConfigDAO;
    private final UserDAO userDAO;
    private final SecureRandom random;

    private final EmailService emailService;
    private final SmsService smsService;
    private final TelegramService telegramService;
    private final FileService fileService;

    public OtpService() {
        this.otpCodeDAO = new OtpCodeDAO();
        this.otpConfigDAO = new OtpConfigDAO();
        this.userDAO = new UserDAO();
        this.random = new SecureRandom();

        this.emailService = new EmailService();
        this.smsService = new SmsService();
        this.telegramService = new TelegramService();
        this.fileService = new FileService();
    }

    public Map<String, Object> generate(Long userId, String operationId, String channel) throws SQLException {
        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        User user = userOpt.get();

        OtpConfig config = otpConfigDAO.getConfig();
        String code = generateRandomCode(config.getCodeLength());
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(config.getTtlSeconds());

        OtpCode otpCode = new OtpCode(code, operationId, userId, expiresAt);
        otpCode = otpCodeDAO.save(otpCode);

        boolean sent = sendCode(user, code, channel);

        Map<String, Object> result = new HashMap<>();
        result.put("operationId", operationId);
        result.put("expiresAt", expiresAt);
        result.put("sent", sent);
        result.put("channel", channel);

        logger.info("Generated OTP for user {}, operation {}, channel {}", userId, operationId, channel);
        return result;
    }

    public Map<String, Object> validate(String operationId, String code) throws SQLException {
        Optional<OtpCode> otpCodeOpt = otpCodeDAO.findByCodeAndOperationId(code, operationId);

        if (otpCodeOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid OTP code");
        }

        OtpCode otpCode = otpCodeOpt.get();

        if (otpCode.getStatus() != OtpStatus.ACTIVE) {
            throw new IllegalArgumentException("OTP code is already " + otpCode.getStatus().name().toLowerCase());
        }

        if (otpCode.isExpired()) {
            otpCodeDAO.updateStatus(otpCode.getId(), OtpStatus.EXPIRED);
            throw new IllegalArgumentException("OTP code has expired");
        }

        otpCodeDAO.updateStatus(otpCode.getId(), OtpStatus.USED);

        Map<String, Object> result = new HashMap<>();
        result.put("valid", true);
        result.put("operationId", operationId);
        result.put("userId", otpCode.getUserId());

        logger.info("Validated OTP for operation {}", operationId);
        return result;
    }

    public OtpConfig getConfig() throws SQLException {
        return otpConfigDAO.getConfig();
    }

    public OtpConfig updateConfig(int ttlSeconds, int codeLength, String updatedBy) throws SQLException {
        if (ttlSeconds < 30 || ttlSeconds > 3600) {
            throw new IllegalArgumentException("TTL must be between 30 and 3600 seconds");
        }
        if (codeLength < 4 || codeLength > 8) {
            throw new IllegalArgumentException("Code length must be between 4 and 8 digits");
        }

        OtpConfig config = new OtpConfig(ttlSeconds, codeLength);
        return otpConfigDAO.updateConfig(config, updatedBy);
    }

    private String generateRandomCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    private boolean sendCode(User user, String code, String channel) {
        switch (channel.toLowerCase()) {
            case "email":
                return emailService.send(user.getEmail(), code);
            case "sms":
                if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                    return smsService.send(user.getPhone(), code);
                }
                logger.warn("No phone number for user: {}", user.getUsername());
                return false;
            case "telegram":
                if (user.getTelegramChatId() != null && !user.getTelegramChatId().isEmpty()) {
                    return telegramService.send(user.getTelegramChatId(), code);
                }
                logger.warn("No telegram chat ID for user: {}", user.getUsername());
                return false;
            case "file":
            default:
                return fileService.save(user.getUsername(), code);
        }
    }
}
