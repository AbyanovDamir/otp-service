package com.promo.otp.service;

import com.promo.otp.dao.OtpCodeDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExpiredCodesScheduler {
    private static final Logger logger = LoggerFactory.getLogger(ExpiredCodesScheduler.class);
    private static ScheduledExecutorService scheduler;
    private static final OtpCodeDAO otpCodeDAO = new OtpCodeDAO();

    public static void start() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                int expired = otpCodeDAO.expireOldCodes();
                if (expired > 0) {
                    logger.debug("Expired {} OTP codes", expired);
                }
            } catch (SQLException e) {
                logger.error("Failed to expire old OTP codes: {}", e.getMessage());
            }
        }, 0, 1, TimeUnit.MINUTES);

        logger.info("Expired codes scheduler started");
    }

    public static void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            logger.info("Expired codes scheduler stopped");
        }
    }
}
