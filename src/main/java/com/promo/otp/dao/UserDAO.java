package com.promo.otp.dao;

import com.promo.otp.config.DatabaseConfig;
import com.promo.otp.model.Role;
import com.promo.otp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    public User save(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, email, phone, telegram_chat_id, role) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING id, created_at, updated_at";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getTelegramChatId());
            stmt.setString(6, user.getRole().name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user.setId(rs.getLong("id"));
                    user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                }
            }
        }

        logger.info("Saved user: {}", user.getUsername());
        return user;
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }

        return Optional.empty();
    }

    public Optional<User> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }

        return Optional.empty();
    }

    public List<User> findAllNonAdmin() throws SQLException {
        String sql = "SELECT * FROM users WHERE role != 'ADMIN' ORDER BY id";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }

        return users;
    }

    public boolean existsAdmin() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }

        return false;
    }

    public boolean deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int affected = stmt.executeUpdate();

            if (affected > 0) {
                logger.info("Deleted user with id: {}", id);
                return true;
            }
        }

        return false;
    }

    public void updateTelegramChatId(Long userId, String chatId) throws SQLException {
        String sql = "UPDATE users SET telegram_chat_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, chatId);
            stmt.setLong(2, userId);
            stmt.executeUpdate();

            logger.info("Updated telegram chat id for user: {}", userId);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setTelegramChatId(rs.getString("telegram_chat_id"));
        user.setRole(Role.fromString(rs.getString("role")));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return user;
    }
}
