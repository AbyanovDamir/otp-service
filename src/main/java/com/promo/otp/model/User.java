package com.promo.otp.model;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private String email;
    private String phone;
    private String telegramChatId;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {}

    public User(String username, String passwordHash, String email, String phone, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getTelegramChatId() { return telegramChatId; }
    public void setTelegramChatId(String telegramChatId) { this.telegramChatId = telegramChatId; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public boolean isAdmin() { return role == Role.ADMIN; }
}
