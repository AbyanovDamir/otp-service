package com.promo.otp.model;

public enum OtpStatus {
    ACTIVE, EXPIRED, USED;

    public static OtpStatus fromString(String status) {
        try {
            return OtpStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
