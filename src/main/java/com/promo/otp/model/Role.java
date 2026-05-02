package com.promo.otp.model;

public enum Role {
    ADMIN, USER;

    public static Role fromString(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return USER;
        }
    }
}
