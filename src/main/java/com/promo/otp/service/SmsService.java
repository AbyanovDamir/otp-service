package com.promo.otp.service;

import org.jsmpp.bean.*;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.SubmitSmResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class SmsService {
    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);
    private final String host;
    private final int port;
    private final String systemId;
    private final String password;
    private final String systemType;
    private final String sourceAddress;
    
    public SmsService() {
        Properties config = loadConfig();
        this.host = config.getProperty("smpp.host", "localhost");
        this.port = Integer.parseInt(config.getProperty("smpp.port", "2775"));
        this.systemId = config.getProperty("smpp.system_id", "smppclient1");
        this.password = config.getProperty("smpp.password", "password");
        this.systemType = config.getProperty("smpp.system_type", "OTP");
        this.sourceAddress = config.getProperty("smpp.source_addr", "OTPService");
        
        logger.info("SMS notification service initialized with host: {}", host);
    }
    
    private Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("sms.properties")) {
            if (input != null) {
                props.load(input);
                logger.info("Loaded SMS configuration");
            } else {
                logger.warn("sms.properties not found, using default values");
            }
        } catch (IOException e) {
            logger.error("Failed to load SMS configuration", e);
        }
        return props;
    }
    
    public boolean send(String phoneNumber, String code) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            logger.error("No phone number provided");
            return false;
        }
        
        String message = String.format("Your OTP verification code is: %s", code);
        SMPPSession session = new SMPPSession();
        
        try {
            BindParameter bindParameter = new BindParameter(
                    BindType.BIND_TX,
                    systemId,
                    password,
                    systemType,
                    TypeOfNumber.UNKNOWN,
                    NumberingPlanIndicator.UNKNOWN,
                    null
            );
            
            session.connectAndBind(host, port, bindParameter);
            
            SubmitSmResult result = session.submitShortMessage(
                    systemType,
                    TypeOfNumber.UNKNOWN,
                    NumberingPlanIndicator.UNKNOWN,
                    sourceAddress,
                    TypeOfNumber.UNKNOWN,
                    NumberingPlanIndicator.UNKNOWN,
                    phoneNumber,
                    new ESMClass(),
                    (byte) 0,
                    (byte) 1,
                    null,
                    null,
                    new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
                    (byte) 0,
                    new GeneralDataCoding(Alphabet.ALPHA_DEFAULT),
                    (byte) 0,
                    message.getBytes(StandardCharsets.UTF_8)
            );
            
            logger.info("SMS sent successfully to: {}, messageId: {}", phoneNumber, result.getMessageId());
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage());
            return false;
        } finally {
            if (session != null && session.getSessionState().isBound()) {
                try {
                    session.unbindAndClose();
                } catch (Exception e) {
                    logger.warn("Error closing SMPP session: {}", e.getMessage());
                }
            }
        }
    }
}
