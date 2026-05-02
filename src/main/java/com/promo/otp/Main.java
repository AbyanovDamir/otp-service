package com.promo.otp;

import com.promo.otp.config.DatabaseConfig;
import com.promo.otp.server.HttpServerManager;
import com.promo.otp.service.ExpiredCodesScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        try {
            logger.info("Starting OTP Service...");
            
            DatabaseConfig.initialize();
            logger.info("Database connection pool initialized");
            
            ExpiredCodesScheduler.start();
            logger.info("Expired codes cleanup scheduler started");
            
            int port = Integer.parseInt(System.getProperty("app.port", "8080"));
            HttpServerManager server = new HttpServerManager(port);
            server.start();
            
            logger.info("OTP Service started successfully on port {}", port);
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down OTP Service...");
                server.stop();
                ExpiredCodesScheduler.stop();
                DatabaseConfig.shutdown();
                logger.info("OTP Service stopped");
            }));
            
        } catch (Exception e) {
            logger.error("Failed to start OTP Service", e);
            System.exit(1);
        }
    }
}
