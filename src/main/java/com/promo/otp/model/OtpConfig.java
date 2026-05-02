package com.promo.otp.model;

import java.time.LocalDateTime;

public class OtpConfig {
    private Long id;
    private int ttlSeconds;
    private int codeLength;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public OtpConfig() {}

    public OtpConfig(int ttlSeconds, int codeLength) {
        this.ttlSeconds = ttlSeconds;
        this.codeLength = codeLength;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getTtlSeconds() { return ttlSeconds; }
    public void setTtlSeconds(int ttlSeconds) { this.ttlSeconds = ttlSeconds; }
    public int getCodeLength() { return codeLength; }
    public void setCodeLength(int codeLength) { this.codeLength = codeLength; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
