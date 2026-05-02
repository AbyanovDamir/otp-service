package com.promo.otp.security;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordUtil {
    private static final Logger logger = LoggerFactory.getLogger(PasswordUtil.class);
    private static final int BCRYPT_ROUNDS = 10;

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            logger.error("Error checking password", e);
            return false;
        }
    }
}
