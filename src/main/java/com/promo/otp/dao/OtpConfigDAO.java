package com.promo.otp.dao;

import com.promo.otp.config.DatabaseConfig;
import com.promo.otp.model.OtpConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class OtpConfigDAO {
    private static final Logger logger = LoggerFactory.getLogger(OtpConfigDAO.class);

    public OtpConfig getConfig() throws SQLException {
        String sql = "SELECT * FROM otp_config LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return mapResultSetToConfig(rs);
            }
        }

        return new OtpConfig(300, 6);
    }

    public OtpConfig updateConfig(OtpConfig config, String updatedBy) throws SQLException {
        String sql = "UPDATE otp_config SET ttl_seconds = ?, code_length = ?, updated_by = ? " +
                     "WHERE id = (SELECT id FROM otp_config LIMIT 1) RETURNING *";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, config.getTtlSeconds());
            stmt.setInt(2, config.getCodeLength());
            stmt.setString(3, updatedBy);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("Updated OTP configuration: ttl={}, length={}",
                               config.getTtlSeconds(), config.getCodeLength());
                    return mapResultSetToConfig(rs);
                }
            }
        }

        throw new SQLException("Failed to update OTP configuration");
    }

    private OtpConfig mapResultSetToConfig(ResultSet rs) throws SQLException {
        OtpConfig config = new OtpConfig();
        config.setId(rs.getLong("id"));
        config.setTtlSeconds(rs.getInt("ttl_seconds"));
        config.setCodeLength(rs.getInt("code_length"));
        config.setUpdatedBy(rs.getString("updated_by"));

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            config.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return config;
    }
}
