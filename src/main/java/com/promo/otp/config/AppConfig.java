package com.promo.otp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream input = AppConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                logger.warn("application.properties not found, using defaults");
                setDefaults();
            }
        } catch (IOException e) {
            logger.error("Failed to load application properties", e);
            setDefaults();
        }
    }

    private static void setDefaults() {
        properties.setProperty("app.port", "8080");
        properties.setProperty("jwt.secret", "default-secret-key-change-in-production");
        properties.setProperty("jwt.expiration.ms", "86400000");
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static long getLong(String key, long defaultValue) {
        try {
            return Long.parseLong(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
