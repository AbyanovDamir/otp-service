package com.promo.otp.model;

import java.time.LocalDateTime;

public class OtpCode {
    private Long id;
    private String code;
    private String operationId;
    private Long userId;
    private OtpStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime validatedAt;

    public OtpCode() {}

    public OtpCode(String code, String operationId, Long userId, LocalDateTime expiresAt) {
        this.code = code;
        this.operationId = operationId;
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.status = OtpStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public OtpStatus getStatus() { return status; }
    public void setStatus(OtpStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }
    public boolean isExpired() { return LocalDateTime.now().isAfter(expiresAt); }
    public boolean isActive() { return status == OtpStatus.ACTIVE && !isExpired(); }
}
