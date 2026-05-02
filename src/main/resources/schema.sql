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

-- ============================================
-- Функция для автоматического назначения ADMIN первому пользователю
-- ============================================
CREATE OR REPLACE FUNCTION set_first_user_as_admin()
RETURNS TRIGGER AS $$
DECLARE
    user_count INTEGER;
BEGIN
    -- Подсчитываем количество существующих пользователей
    SELECT COUNT(*) INTO user_count FROM users;

    -- Если это первый пользователь, назначаем ему роль ADMIN
    IF user_count = 0 THEN
        NEW.role := 'ADMIN';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Создаём триггер, который срабатывает перед вставкой нового пользователя
DROP TRIGGER IF EXISTS trigger_set_admin_on_first_user ON users;
CREATE TRIGGER trigger_set_admin_on_first_user
    BEFORE INSERT ON users
    FOR EACH ROW
    EXECUTE FUNCTION set_first_user_as_admin();

-- ============================================
-- Альтернативный подход: создаём администратора по умолчанию (если нужно)
-- Раскомментируйте, если хотите создать предустановленного администратора
-- ============================================
-- INSERT INTO users (username, password_hash, email, phone, role)
-- VALUES (
--     'admin',
--     '$2a$10$YourHashedPasswordHere', -- Замените на реальный хеш
--     'admin@otp-service.com',
--     '+79990000000',
--     'ADMIN'
-- ) ON CONFLICT (username) DO NOTHING;

-- Insert default OTP configuration
INSERT INTO otp_config (ttl_seconds, code_length, updated_by) VALUES (300, 6, 'system')
ON CONFLICT DO NOTHING;

-- ============================================
-- Создаём функцию для ручного повышения пользователя до ADMIN (полезно для тестов)
-- ============================================
CREATE OR REPLACE FUNCTION promote_to_admin(user_id_param INTEGER)
RETURNS TABLE(username VARCHAR, old_role VARCHAR, new_role VARCHAR) AS $$
DECLARE
    user_username VARCHAR;
    current_role VARCHAR;
BEGIN
    -- Получаем текущие данные пользователя
    SELECT u.username, u.role INTO user_username, current_role
    FROM users u WHERE u.id = user_id_param;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'User with id % not found', user_id_param;
    END IF;

    -- Обновляем роль
    UPDATE users SET role = 'ADMIN' WHERE id = user_id_param;

    -- Возвращаем результат
    username := user_username;
    old_role := current_role;
    new_role := 'ADMIN';
    RETURN NEXT;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- Создаём функцию для просмотра статистики (для админов)
-- ============================================
CREATE OR REPLACE FUNCTION get_otp_statistics()
RETURNS TABLE(
    total_users INTEGER,
    admin_count INTEGER,
    user_count INTEGER,
    total_otp_generated INTEGER,
    active_otp_count INTEGER,
    expired_otp_count INTEGER,
    used_otp_count INTEGER
) AS $$
BEGIN
    -- Количество пользователей
    SELECT COUNT(*) INTO total_users FROM users;
    SELECT COUNT(*) INTO admin_count FROM users WHERE role = 'ADMIN';
    SELECT COUNT(*) INTO user_count FROM users WHERE role = 'USER';

    -- Статистика по OTP кодам
    SELECT COUNT(*) INTO total_otp_generated FROM otp_codes;
    SELECT COUNT(*) INTO active_otp_count FROM otp_codes WHERE status = 'ACTIVE';
    SELECT COUNT(*) INTO expired_otp_count FROM otp_codes WHERE status = 'EXPIRED';
    SELECT COUNT(*) INTO used_otp_count FROM otp_codes WHERE status = 'USED';

    RETURN NEXT;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- Комментарии к таблицам и колонкам
-- ============================================
COMMENT ON TABLE users IS 'Таблица пользователей системы';
COMMENT ON COLUMN users.role IS 'Роль пользователя: ADMIN - администратор, USER - обычный пользователь';
COMMENT ON TABLE otp_config IS 'Конфигурация OTP (TTL и длина кода)';
COMMENT ON TABLE otp_codes IS 'Хранилище сгенерированных OTP кодов';
COMMENT ON FUNCTION set_first_user_as_admin() IS 'Автоматически назначает роль ADMIN первому зарегистрированному пользователю';
COMMENT ON FUNCTION promote_to_admin(INTEGER) IS 'Функция для повышения пользователя до администратора';
COMMENT ON FUNCTION get_otp_statistics() IS 'Возвращает статистику по пользователям и OTP кодам';
