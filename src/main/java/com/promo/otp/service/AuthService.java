package com.promo.otp.service;

import com.promo.otp.dao.UserDAO;
import com.promo.otp.model.Role;
import com.promo.otp.model.User;
import com.promo.otp.security.JwtUtil;
import com.promo.otp.security.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public Map<String, Object> register(String username, String password, String email, String phone) throws SQLException {
        Optional<User> existing = userDAO.findByUsername(username);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        Role role = Role.USER;
        if (!userDAO.existsAdmin()) {
            role = Role.ADMIN;
            logger.info("First user registered as ADMIN: {}", username);
        }

        String passwordHash = PasswordUtil.hashPassword(password);
        User user = new User(username, passwordHash, email, phone, role);
        user = userDAO.save(user);

        String token = JwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("token", token);

        logger.info("User registered successfully: {}", username);
        return result;
    }

    public Map<String, Object> login(String username, String password) throws SQLException {
        Optional<User> userOpt = userDAO.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        User user = userOpt.get();
        if (!PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = JwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("token", token);

        logger.info("User logged in successfully: {}", username);
        return result;
    }

    public Optional<User> getUserById(Long id) throws SQLException {
        return userDAO.findById(id);
    }
}
