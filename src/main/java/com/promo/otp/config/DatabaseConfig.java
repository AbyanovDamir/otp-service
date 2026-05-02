package com.promo.otp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static HikariDataSource dataSource;
    
    public static void initialize() {
        Properties props = new Properties();
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input != null) {
                props.load(input);
            }
            
            String dbUrl = props.getProperty("db.url", "jdbc:postgresql://postgres:5432/otp_service");
            String dbUser = props.getProperty("db.username", "otp_user");
            String dbPass = props.getProperty("db.password", "otp_secure_password");
            
            logger.info("Connecting to database: {}", dbUrl);
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);
            config.setUsername(dbUser);
            config.setPassword(dbPass);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            config.setConnectionTestQuery("SELECT 1");
            config.setPoolName("OTP-HikariPool");
            
            dataSource = new HikariDataSource(config);
            logger.info("Database connection pool created successfully");
            
            // Test connection
            try (Connection conn = getConnection()) {
                logger.info("Database connection test successful");
            }
            
        } catch (Exception e) {
            logger.error("Failed to load database configuration", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            initialize();
        }
        return dataSource.getConnection();
    }
    
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }
}
