package com.promo.otp.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final Session session;
    private final String fromEmail;
    
    public EmailService() {
        Properties props = new Properties();
        Properties config = loadConfig();
        
        String host = config.getProperty("mail.smtp.host", "172.18.0.2");
        int port = Integer.parseInt(config.getProperty("mail.smtp.port", "1025"));
        
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "false");
        
        this.fromEmail = config.getProperty("email.from", "otp@test.com");
        this.session = Session.getInstance(props, null);
        
        logger.info("Email service configured with host: {}:{}", host, port);
    }
    
    private Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("email.properties")) {
            if (input != null) {
                props.load(input);
                logger.info("Loaded email configuration");
            }
        } catch (Exception e) {
            logger.warn("Failed to load email.properties, using defaults");
        }
        return props;
    }
    
    public boolean send(String toEmail, String code) {
        if (toEmail == null || toEmail.isEmpty()) {
            logger.error("No email address provided");
            return false;
        }
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject("Your OTP Verification Code");
            message.setText(String.format(
                "Hello,\n\nYour OTP verification code is: %s\n\n" +
                "This code will expire in 5 minutes.\n\n" +
                "Best regards,\nOTP Service Team",
                code
            ));
            
            Transport.send(message);
            logger.info("Email sent successfully to: {}", toEmail);
            return true;
            
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            return false;
        }
    }
}
