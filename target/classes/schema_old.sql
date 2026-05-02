-- ============================================
-- OTP Service Database Schema
-- PostgreSQL 17
-- ============================================

DROP TABLE IF EXISTS otp_codes CASCADE;
DROP TABLE IF EXISTS otp_config CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Users table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    telegram_chat_id VARCHAR(50),
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'USER')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- OTP Configuration table (always exactly one row)
CREATE TABLE otp_config (
    id SERIAL PRIMARY KEY,
    ttl_seconds INTEGER NOT NULL DEFAULT 300,
    code_length INTEGER NOT NULL DEFAULT 6,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50)
);

-- OTP Codes table
CREATE TABLE otp_codes (
    id SERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL,
    operation_id VARCHAR(100) NOT NULL,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'EXPIRED', 'USED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    validated_at TIMESTAMP
);

-- Indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_otp_codes_code ON otp_codes(code);
CREATE INDEX idx_otp_codes_user_id ON otp_codes(user_id);
CREATE INDEX idx_otp_codes_operation_id ON otp_codes(operation_id);
CREATE INDEX idx_otp_codes_status_expires ON otp_codes(status, expires_at);

-- Update updated_at trigger
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_otp_config_updated_at BEFORE UPDATE ON otp_config FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert default OTP configuration
INSERT INTO otp_config (ttl_seconds, code_length, updated_by) VALUES (300, 6, 'system');

-- NO DEFAULT ADMIN USER - first registered user will be ADMIN
