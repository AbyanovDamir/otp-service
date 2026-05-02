package com.promo.otp.dao;

import com.promo.otp.config.DatabaseConfig;
import com.promo.otp.model.OtpCode;
import com.promo.otp.model.OtpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OtpCodeDAO {
    private static final Logger logger = LoggerFactory.getLogger(OtpCodeDAO.class);

    public OtpCode save(OtpCode otpCode) throws SQLException {
        String sql = "INSERT INTO otp_codes (code, operation_id, user_id, status, expires_at) " +
                     "VALUES (?, ?, ?, ?, ?) RETURNING id, created_at";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, otpCode.getCode());
            stmt.setString(2, otpCode.getOperationId());
            stmt.setLong(3, otpCode.getUserId());
            stmt.setString(4, otpCode.getStatus().name());
            stmt.setTimestamp(5, Timestamp.valueOf(otpCode.getExpiresAt()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    otpCode.setId(rs.getLong("id"));
                    otpCode.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
            }
        }

        logger.info("Saved OTP code for operation: {}, user: {}",
                   otpCode.getOperationId(), otpCode.getUserId());
        return otpCode;
    }

    public Optional<OtpCode> findByCodeAndOperationId(String code, String operationId) throws SQLException {
        String sql = "SELECT * FROM otp_codes WHERE code = ? AND operation_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            stmt.setString(2, operationId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOtpCode(rs));
                }
            }
        }

        return Optional.empty();
    }

    public void updateStatus(Long id, OtpStatus status) throws SQLException {
        String sql = "UPDATE otp_codes SET status = ?, validated_at = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            if (status == OtpStatus.USED) {
                stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            } else {
                stmt.setTimestamp(2, null);
            }
            stmt.setLong(3, id);

            stmt.executeUpdate();
            logger.info("Updated OTP code {} status to {}", id, status);
        }
    }

    public int expireOldCodes() throws SQLException {
        String sql = "UPDATE otp_codes SET status = 'EXPIRED' " +
                     "WHERE status = 'ACTIVE' AND expires_at < ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            int updated = stmt.executeUpdate();

            if (updated > 0) {
                logger.info("Expired {} OTP codes", updated);
            }

            return updated;
        }
    }

    public List<OtpCode> findByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM otp_codes WHERE user_id = ? ORDER BY created_at DESC";
        List<OtpCode> codes = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    codes.add(mapResultSetToOtpCode(rs));
                }
            }
        }

        return codes;
    }

    public void deleteByUserId(Long userId) throws SQLException {
        String sql = "DELETE FROM otp_codes WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            int deleted = stmt.executeUpdate();
            logger.info("Deleted {} OTP codes for user {}", deleted, userId);
        }
    }

    private OtpCode mapResultSetToOtpCode(ResultSet rs) throws SQLException {
        OtpCode otpCode = new OtpCode();
        otpCode.setId(rs.getLong("id"));
        otpCode.setCode(rs.getString("code"));
        otpCode.setOperationId(rs.getString("operation_id"));
        otpCode.setUserId(rs.getLong("user_id"));
        otpCode.setStatus(OtpStatus.fromString(rs.getString("status")));
        otpCode.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        otpCode.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());

        Timestamp validatedAt = rs.getTimestamp("validated_at");
        if (validatedAt != null) {
            otpCode.setValidatedAt(validatedAt.toLocalDateTime());
        }

        return otpCode;
    }
}
