package com.promo.otp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    private static final String OTP_FILES_DIR = "./otp-files";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public FileService() {
        try {
            Path dir = Paths.get(OTP_FILES_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
                logger.info("Created OTP files directory: {}", OTP_FILES_DIR);
            }
        } catch (IOException e) {
            logger.error("Failed to create OTP files directory: {}", e.getMessage());
        }
    }

    public boolean save(String username, String code) {
        String filename = String.format("%s/otp_%s_%d.txt",
                OTP_FILES_DIR,
                username,
                System.currentTimeMillis());

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            writer.println("========================================");
            writer.println("OTP Code Generated");
            writer.println("Timestamp: " + LocalDateTime.now().format(DATE_FORMATTER));
            writer.println("User: " + username);
            writer.println("Code: " + code);
            writer.println("========================================");
            writer.println();

            logger.info("OTP code saved to file: {}", filename);
            return true;

        } catch (IOException e) {
            logger.error("Failed to save OTP code to file: {}", e.getMessage());
            return false;
        }
    }
}
