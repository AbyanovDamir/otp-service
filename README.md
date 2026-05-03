# OTP Service

## 📋 Описание проекта

**OTP Service** — это backend-приложение для генерации и проверки одноразовых кодов (OTP) с поддержкой нескольких каналов доставки. Сервис обеспечивает дополнительный уровень безопасности при выполнении операций, требующих подтверждения.

### 🎯 Основные возможности

| Функция | Описание |
|---------|----------|
| 🔐 **JWT аутентификация** | Регистрация и вход с получением токена |
| 👥 **Ролевая модель** | Разделение на администраторов и обычных пользователей |
| 🔢 **Генерация OTP** | Создание уникальных кодов для операций |
| ✅ **Валидация OTP** | Проверка кодов с отслеживанием статусов (ACTIVE/EXPIRED/USED) |
| 📧 **Email рассылка** | Отправка кодов через SMTP (MailHog для тестирования) |
| 📱 **SMS рассылка** | Отправка через SMPP эмулятор |
| 🤖 **Telegram рассылка** | Отправка через Telegram Bot API (эмулятор) |
| 💾 **Сохранение в файл** | Альтернативный канал доставки |
| ⏰ **Автоматическое протухание** | Периодическая очистка просроченных кодов |
| 🐳 **Docker поддержка** | Контейнеризация приложения и базы данных |

---

## 🛠 Технологический стек

| Компонент | Технология | Версия |
|-----------|------------|--------|
| **Язык** | Java | 21 |
| **Сборка** | Maven | 3.8+ |
| **База данных** | PostgreSQL | 17 |
| **Доступ к БД** | JDBC + HikariCP | - |
| **Встроенный сервер** | com.sun.net.httpserver | - |
| **Аутентификация** | JWT (jjwt) | 0.11.5 |
| **Хеширование** | BCrypt | 0.4 |
| **Email** | Jakarta Mail (Angus) | 2.0.2 |
| **SMS** | JSMPP | 2.2.4 |
| **Telegram** | Telegram Bot API | 6.9.0 |
| **Логирование** | Logback | 1.4.14 |
| **JSON** | Jackson | 2.15.3 |
| **Контейнеризация** | Docker + Docker Compose | 3.8 |

---

## 📁 Структура проекта

```
otp-service/
├── 📁 docker/ # Docker конфигурации
│ ├── docker-compose.yml # Оркестрация сервисов
│ ├── docker-compose.prod.yml # Продакшн конфигурация
│ └── Dockerfile # Сборка образа
│
├── 📁 scripts/ # Вспомогательные скрипты
│ ├── docker-start.sh # Запуск контейнеров
│ ├── docker-stop.sh # Остановка контейнеров
│ ├── docker-restart.sh # Перезапуск
│ └── docker-logs.sh # Просмотр логов
│
├── 📁 src/main/
│ ├── 📁 java/com/promo/otp/
│ │ ├── Main.java # Точка входа
│ │ ├── 📁 config/ # Конфигурация
│ │ │ ├── AppConfig.java
│ │ │ └── DatabaseConfig.java
│ │ ├── 📁 controller/ # HTTP обработчики
│ │ │ ├── AdminController.java
│ │ │ ├── AuthController.java
│ │ │ └── OtpController.java
│ │ ├── 📁 dao/ # Доступ к данным
│ │ │ ├── OtpCodeDAO.java
│ │ │ ├── OtpConfigDAO.java
│ │ │ └── UserDAO.java
│ │ ├── 📁 model/ # Entity (Java Records)
│ │ │ ├── ApiResponse.java
│ │ │ ├── OtpCode.java
│ │ │ ├── OtpConfig.java
│ │ │ ├── OtpStatus.java
│ │ │ ├── Role.java
│ │ │ └── User.java
│ │ ├── 📁 security/ # Безопасность
│ │ │ ├── AuthFilter.java
│ │ │ ├── JwtUtil.java
│ │ │ └── PasswordUtil.java
│ │ ├── 📁 server/ # HTTP сервер
│ │ │ └── HttpServerManager.java
│ │ ├── 📁 service/ # Бизнес-логика
│ │ │ ├── AuthService.java
│ │ │ ├── EmailService.java
│ │ │ ├── ExpiredCodesScheduler.java
│ │ │ ├── FileService.java
│ │ │ ├── OtpService.java
│ │ │ ├── SmsService.java
│ │ │ └── TelegramService.java
│ │ └── 📁 util/
│ │ └── JsonUtil.java
│ │
│ └── 📁 resources/
│ ├── application.properties # Основные настройки
│ ├── schema.sql # Схема БД
│ ├── schema_old.sql # Старая схема
│ ├── logback.xml # Настройки логирования
│ ├── email.properties # SMTP настройки
│ ├── sms.properties # SMPP настройки
│ └── telegram.properties # Telegram настройки
│
├── 📁 otp-files/ # Сгенерированные OTP файлы
├── 📁 logs/ # Логи приложения
│ ├── otp-service.log # Основной лог
│ ├── otp-service-error.log # Лог ошибок
│ └── otp-service-*.log # Ротированные логи
│
├── 📁 target/ # Скомпилированные файлы
├── pom.xml # Maven конфигурация
├── Makefile # Автоматизация сборки
├── test.sh # Автотест API
├── dependency-reduced-pom.xml # Оптимизированный pom
└── README.md # Документация

```

---
### Реализованные функции

| № | Функция | Статус |
|---|---------|--------|
| 1 | Регистрация и аутентификация (JWT) | ✅ |
| 2 | Ролевая модель (USER / ADMIN) | ✅ |
| 3 | Генерация OTP кода | ✅ |
| 4 | Валидация OTP кода | ✅ |
| 5 | Отправка OTP через FILE канал | ✅ |
| 6 | Отправка OTP через SMS канал (SMPP) | ✅ |
| 7 | Отправка OTP через EMAIL канал (SMTP) | ✅ |
| 8 | Отправка OTP через TELEGRAM канал | ✅ |
| 9 | Управление TTL и длиной кода (админ) | ✅ |
| 10 | Автоматическая очистка просроченных кодов | ✅ |
| 11 | Просмотр списка пользователей (админ) | ✅ |
| 12 | Health check endpoint | ✅ |

---

## 📋 Детальное описание функций

### 1. Регистрация и аутентификация (JWT)
- Регистрация новых пользователей с username, password, email, phone
- Хеширование паролей с использованием BCrypt
- Генерация JWT токенов при успешной аутентификации
- Валидация JWT токенов для защищенных endpoint'ов

### 2. Ролевая модель (USER / ADMIN)
- **USER** - может генерировать и проверять OTP коды
- **ADMIN** - все права USER + управление конфигурацией + просмотр пользователей
- Проверка роли через AuthFilter

### 3. Генерация OTP кода
- Создание случайного числового кода (длина 6-8 цифр)
- Привязка к operationId для каждой операции
- Поддержка разных каналов доставки
- Сохранение в БД с статусом ACTIVE

### 4. Валидация OTP кода
- Проверка существования кода по operationId
- Проверка срока действия (expires_at)
- Проверка статуса (ACTIVE)
- Обновление статуса на USED после успешной проверки

### 5. Отправка OTP через FILE канал
- Сохранение OTP кода в текстовый файл
- Директория: `otp-files/`
- Формат файла: `otp_{operationId}_{timestamp}.txt`

### 6. Отправка OTP через SMS канал (SMPP)
- Протокол SMPP 3.4
- Интеграция с JSMPP библиотекой
- Поддержка эмулятора smpp-smsc-simulator
- Асинхронная отправка сообщений

### 7. Отправка OTP через EMAIL канал (SMTP)
- Протокол SMTP
- Интеграция с Jakarta Mail
- Поддержка MailHog для тестирования
- Отправка HTML писем

### 8. Отправка OTP через TELEGRAM канал
- Telegram Bot API
- Интеграция с Telegram Bots библиотекой
- Поддержка эмулятора telegram-emulator
- Отправка форматированных сообщений

### 9. Управление TTL и длиной кода (админ)
- Просмотр текущей конфигурации: `GET /api/admin/config`
- Обновление конфигурации: `PUT /api/admin/config`
- TTL диапазон: 60-3600 секунд
- Длина кода: 4-10 цифр

### 10. Автоматическая очистка просроченных кодов
- Фоновый планировщик (каждые 5 минут)
- Удаление кодов с expires_at < NOW()
- Обновление статуса ACTIVE → EXPIRED

### 11. Просмотр списка пользователей (админ)
- `GET /api/admin/users`
- Только для пользователей с ролью ADMIN
- Возвращает id, username, email, phone, role, created_at

### 12. Health check endpoint
- `GET /api/health`
- Публичный endpoint без авторизации
- Проверка статуса сервиса

---

## 🛠 Технологический стек

| Компонент | Технология | Версия |
|-----------|------------|--------|
| Язык | Java | 21 |
| HTTP сервер | com.sun.net.httpserver | - |
| Аутентификация | JWT (jjwt) | 0.11.5 |
| Хеширование | BCrypt | 0.4 |
| База данных | PostgreSQL | 17 |
| Пул соединений | HikariCP | - |
| Email | Jakarta Mail (Angus) | 2.0.2 |
| SMS | JSMPP | 2.2.4 |
| Telegram | Telegram Bot API | 6.9.0 |
| JSON | Jackson | 2.15.3 |
| Логирование | Logback | 1.4.14 |
| Сборка | Maven | 3.8+ |
| Контейнеризация | Docker + Docker Compose | 3.8 |

---

## 📊 Структура базы данных

### Таблица `users`
| Поле | Тип | Описание |
|------|-----|----------|
| id | SERIAL | PRIMARY KEY |
| username | VARCHAR(100) | UNIQUE NOT NULL |
| password_hash | VARCHAR(255) | NOT NULL |
| email | VARCHAR(255) | NOT NULL |
| phone | VARCHAR(20) | - |
| role | VARCHAR(50) | DEFAULT 'USER' |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

### Таблица `otp_config`
| Поле | Тип | Описание |
|------|-----|----------|
| id | SERIAL | PRIMARY KEY |
| ttl_seconds | INTEGER | DEFAULT 300 |
| code_length | INTEGER | DEFAULT 6 |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |
| updated_by | INTEGER | REFERENCES users(id) |

### Таблица `otp_codes`
| Поле | Тип | Описание |
|------|-----|----------|
| id | SERIAL | PRIMARY KEY |
| user_id | INTEGER | REFERENCES users(id) |
| operation_id | VARCHAR(255) | NOT NULL |
| code | VARCHAR(10) | NOT NULL |
| channel | VARCHAR(50) | NOT NULL |
| status | VARCHAR(20) | DEFAULT 'ACTIVE' |
| expires_at | TIMESTAMP | NOT NULL |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

---

## 🔌 API Endpoints

| Метод | Endpoint | Описание | Аутентификация | Роль |
|-------|----------|----------|----------------|------|
| POST | `/api/auth/register` | Регистрация | Нет | - |
| POST | `/api/auth/login` | Вход | Нет | - |
| GET | `/api/health` | Health check | Нет | - |
| POST | `/api/otp/generate` | Генерация OTP | JWT | USER/ADMIN |
| POST | `/api/otp/validate` | Проверка OTP | JWT | USER/ADMIN |
| GET | `/api/admin/config` | Получить конфигурацию | JWT | ADMIN |
| PUT | `/api/admin/config` | Обновить конфигурацию | JWT | ADMIN |
| GET | `/api/admin/users` | Список пользователей | JWT | ADMIN |

---


---
### Запуск проекта и тестирование
#### Требования

- Docker (20.10+), Docker Compose (2.0+)
- Java 21 (для сборки)
- Maven 3.8+

#### Установка и запуск

```bash

echo "===  Создание общей сети ==="
docker network create otp-network

echo "===  Запуск MailHog ==="
docker run -d -p 1025:1025 -p 8025:8025 --name mailhog --network otp-network mailhog/mailhog

cd ~
git clone https://github.com/melroselabs/smpp-smsc-simulator.git
echo "=== Запуск SMPP симулятора ==="
cd smpp-smsc-simulator
docker compose down
docker compose up -d
docker network connect otp-network smpp-smsc-simulator-smscsimulator-1
cd ~


git clone https://github.com/positron48/telegram-emulator.git
echo "=== Запуск telegram эмулятора ==="
cd telegram-emulator

# Создание Dockerfile



cat > Dockerfile << 'EOF'
# Этап сборки
FROM ubuntu:24.04 AS builder

RUN apt-get update && apt-get install -y wget gcc g++ make git ca-certificates && rm -rf /var/lib/apt/lists/*

ENV GO_VERSION=1.22.2
RUN wget -q https://golang.org/dl/go${GO_VERSION}.linux-amd64.tar.gz \
    && tar -C /usr/local -xzf go${GO_VERSION}.linux-amd64.tar.gz \
    && rm go${GO_VERSION}.linux-amd64.tar.gz

ENV PATH=$PATH:/usr/local/go/bin
ENV GO111MODULE=on
ENV CGO_ENABLED=1

WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download && go mod tidy
COPY . .
RUN go build -o telegram-emulator cmd/emulator/main.go

FROM ubuntu:24.04

RUN apt-get update && apt-get install -y ca-certificates sqlite3 curl && rm -rf /var/lib/apt/lists/*
RUN mkdir -p /app/data

WORKDIR /app
COPY --from=builder /app/telegram-emulator .
COPY --from=builder /app/web ./web

EXPOSE 3001 8087

ENV GIN_MODE=release
ENV TELEGRAM_EMULATOR_HOST=0.0.0.0
ENV TELEGRAM_EMULATOR_PORT=3001

CMD ["./telegram-emulator"]
EOF

#4. Создание конфигурационного файла


# Создаем конфиг с отключенными логами
cat > config.yaml << 'EOF'
emulator:
  host: 0.0.0.0
  port: 3001

database:
  url: /app/data/emulator.db

logging:
  level: info
  format: text
  file: ""

server:
  host: 0.0.0.0
  port: 3001
EOF


#5. Сборка Docker образа


docker build -t telegram-emulator:latest .

#6. Остановка и удаление старого контейнера (если есть)


docker stop telegram-emulator 2>/dev/null || true
docker rm telegram-emulator 2>/dev/null || true

#7. Запуск контейнера


# Запускаем с монтированием конфига
docker run -d \
  --name telegram-emulator \
  --network otp-network \
  -p 3001:3001 \
  -p 8087:8087 \
  -v telegram-emulator-data:/app/data \
  -v $(pwd)/config.yaml:/app/config.yaml \
  telegram-emulator:latest


# Проверка работы


docker ps | grep telegram-emulator
docker logs telegram-emulator

#Тестирование API


# Получить пользователей
curl -s http://localhost:3001/api/users | head -c 500

# Получить чаты
curl -s http://localhost:3001/api/chats | head -c 500

cd ~
# Клонирование реппозитория основных сервисов
git clone https://github.com/AbyanovDamir/otp-service.git
cd otp-service
mvn clean package -DskipTests -q
echo "===  Запуск всех сервисов ==="
cd ~
cd otp-service/docker

docker compose up -d --build

echo "Ожидание запуска всех сервисов (45 сек)..."
sleep 45

```


#### Проверка работоспособности

```bash
# Финальная проверка всех контейнеров
echo "=== Статус всех контейнеров ==="
docker ps

# Проверка PostgreSQL
echo "=== Проверка PostgreSQL ==="
if docker ps | grep -q otp-postgres; then
    echo "✅ PostgreSQL запущен"
else
    echo "⚠️ PostgreSQL не запущен"
    docker logs otp-postgres --tail 30 2>/dev/null || echo "Контейнер не найден"
fi

# Проверка доступности OTP сервиса
echo "=== Проверка доступности OTP API ==="
sleep 5
if curl -s http://localhost:8080/api/health > /dev/null; then
    echo "✅ OTP сервис доступен"
    curl -s http://localhost:8080/api/health | head -1
else
    echo "⚠️ OTP сервис не отвечает, проверьте логи:"
    docker logs otp-service --tail 30 2>/dev/null || echo "Контейнер OTP сервиса не найден"
fi



```
#### В другом терминале — тестирование
```bash
cd ~
cd otp-service
sudo chmod +x test.sh
sudo ./test.sh

```

**Результат теста:**

```
==========================================
ТЕСТИРОВАНИЕ OTP SERVICE
==========================================\033[0m
🔑 Уникальный ID запуска: 1777746568_1257
👤 Администратор: admin_1777746568_1257
👤 Пользователь: user_1777746568_1257

Проверка доступности сервиса...
Сервис доступен

>>> ТЕСТ 1: Health check
📤 ЗАПРОС: GET http://localhost:8080/api/health
📥 ОТВЕТ: {
  "success" : true,
  "message" : "Service is running",
  "data" : null,
  "error" : null
}
HTTP Code: 200
✓ ПРОЙДЕН: Health check endpoint

>>> ТЕСТ 2: Регистрация администратора
📤 ЗАПРОС: POST http://localhost:8080/api/auth/register
📦 ТЕЛО: {"username":"admin_1777746568_1257","password":"admin123","email":"admin_1777746568_1257@test.com","phone":"+79991257746568"}
📥 ОТВЕТ: {
  "success" : true,
  "message" : "User registered successfully",
  "data" : {
    "user" : {
      "id" : 1,
      "username" : "admin_1777746568_1257",
      "passwordHash" : "$2a$10$wRJWeqti5qBaUzauk0YAnujL.m9g1.7dsBuwoSCvbU4VjutIDIKyK",
      "email" : "admin_1777746568_1257@test.com",
      "phone" : "+79991257746568",
      "telegramChatId" : null,
      "role" : "ADMIN",
      "createdAt" : "2026-05-02T18:29:29.119572",
      "updatedAt" : "2026-05-02T18:29:29.119572",
      "admin" : true
    },
    "token" : "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbl8xNzc3NzQ2NTY4XzEyNTciLCJ1c2VySWQiOjEsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc3Nzc0NjU2OSwiZXhwIjoxNzc3ODMyOTY5fQ.Y3_YmPOyuWV-ePH8NsMC2KsYQz8933WjKAQY61SFmZj1O8PpdeI7ml4OM3-TPD_Z"
  },
  "error" : null
}
HTTP Code: 201

🔧 Проверка прав администратора...
✓ Уже имеет роль ADMIN
✓ ПРОЙДЕН: Регистрация/вход администратора (уже ADMIN)
  ✓ Токен администратора получен и активен

>>> ТЕСТ 4: Получение конфигурации OTP
📤 ЗАПРОС: GET http://localhost:8080/api/admin/config (Authorization: Bearer ***)
📥 ОТВЕТ: {
  "success" : true,
  "message" : "Config retrieved",
  "data" : {
    "id" : 1,
    "ttlSeconds" : 300,
    "codeLength" : 6,
    "updatedAt" : "2026-05-02T18:21:48.876832",
    "updatedBy" : "system"
  },
  "error" : null
}
HTTP Code: 200
✓ ПРОЙДЕН: Получение конфигурации
  Текущая конфигурация: TTL=300с, Длина=6 цифр

>>> ТЕСТ 5: Обновление конфигурации OTP
📤 ЗАПРОС: PUT http://localhost:8080/api/admin/config
📦 ТЕЛО: {"ttlSeconds":900,"codeLength":8}
📥 ОТВЕТ: {
  "success" : true,
  "message" : "Config updated successfully",
  "data" : {
    "id" : 1,
    "ttlSeconds" : 900,
    "codeLength" : 8,
    "updatedAt" : "2026-05-02T18:29:29.554522",
    "updatedBy" : "admin_1777746568_1257"
  },
  "error" : null
}
HTTP Code: 200
✓ ПРОЙДЕН: Обновление конфигурации

>>> ТЕСТ 6: Регистрация обычного пользователя
📤 ЗАПРОС: POST http://localhost:8080/api/auth/register
📦 ТЕЛО: {"username":"user_1777746568_1257","password":"user123","email":"user_1777746568_1257@test.com","phone":"+78881257746568"}
📥 ОТВЕТ: {
  "success" : true,
  "message" : "User registered successfully",
  "data" : {
    "user" : {
      "id" : 2,
      "username" : "user_1777746568_1257",
      "passwordHash" : "$2a$10$hpWJkoPk0xbzEMuO8c70s.R4JbR/uDFNiNrzFmkp6I6ixAgsS6N9W",
      "email" : "user_1777746568_1257@test.com",
      "phone" : "+78881257746568",
      "telegramChatId" : null,
      "role" : "USER",
      "createdAt" : "2026-05-02T18:29:29.710561",
      "updatedAt" : "2026-05-02T18:29:29.710561",
      "admin" : false
    },
    "token" : "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ1c2VyXzE3Nzc3NDY1NjhfMTI1NyIsInVzZXJJZCI6Miwicm9sZSI6IlVTRVIiLCJpYXQiOjE3Nzc3NDY1NjksImV4cCI6MTc3NzgzMjk2OX0.xRQqoKbTCUuq2EeOnBwXC_-s55jpl7DaPkG3kMSUA3yxlXjeGwpBEUGoBIYf_uST"
  },
  "error" : null
}
HTTP Code: 201
✓ ПРОЙДЕН: Регистрация пользователя

>>> ТЕСТ 7: Вход обычного пользователя
📤 ЗАПРОС: POST http://localhost:8080/api/auth/login
📦 ТЕЛО: {"username":"user_1777746568_1257","password":"user123"}
📥 ОТВЕТ: {
  "success" : true,
  "message" : "Login successful",
  "data" : {
    "user" : {
      "id" : 2,
      "username" : "user_1777746568_1257",
      "passwordHash" : "$2a$10$hpWJkoPk0xbzEMuO8c70s.R4JbR/uDFNiNrzFmkp6I6ixAgsS6N9W",
      "email" : "user_1777746568_1257@test.com",
      "phone" : "+78881257746568",
      "telegramChatId" : null,
      "role" : "USER",
      "createdAt" : "2026-05-02T18:29:29.710561",
      "updatedAt" : "2026-05-02T18:29:29.710561",
      "admin" : false
    },
    "token" : "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ1c2VyXzE3Nzc3NDY1NjhfMTI1NyIsInVzZXJJZCI6Miwicm9sZSI6IlVTRVIiLCJpYXQiOjE3Nzc3NDY1NjksImV4cCI6MTc3NzgzMjk2OX0.xRQqoKbTCUuq2EeOnBwXC_-s55jpl7DaPkG3kMSUA3yxlXjeGwpBEUGoBIYf_uST"
  },
  "error" : null
}
HTTP Code: 200
✓ ПРОЙДЕН: Вход пользователя
  ✓ Токен пользователя получен

>>> ТЕСТ 8: Генерация OTP кода (канал: file)
📤 ЗАПРОС: POST http://localhost:8080/api/otp/generate
📦 ТЕЛО: {"operationId":"test_file_1777746568_1257","channel":"file"}
📥 ОТВЕТ: {
  "success" : true,
  "message" : "OTP generated successfully",
  "data" : {
    "channel" : "file",
    "operationId" : "test_file_1777746568_1257",
    "sent" : true,
    "expiresAt" : "2026-05-02T18:44:29.894176231"
  },
  "error" : null
}
HTTP Code: 200
✓ ПРОЙДЕН: Генерация OTP (file)

>>> ТЕСТ 9: Генерация OTP кода (канал: sms)
📤 ЗАПРОС: POST http://localhost:8080/api/otp/generate
📦 ТЕЛО: {"operationId":"test_sms_1777746568_1257","channel":"sms"}
📥 ОТВЕТ: {
  "success" : true,
  "message" : "OTP generated successfully",
  "data" : {
    "channel" : "sms",
    "operationId" : "test_sms_1777746568_1257",
    "sent" : true,
    "expiresAt" : "2026-05-02T18:44:30.001064281"
  },
  "error" : null
}
HTTP Code: 200
✓ ПРОЙДЕН: Генерация OTP (sms)

>>> ТЕСТ 10: Генерация OTP кода (канал: email)
📤 ЗАПРОС: POST http://localhost:8080/api/otp/generate
📦 ТЕЛО: {"operationId":"test_email_1777746568_1257","channel":"email"}
📥 ОТВЕТ: {
  "success" : true,
  "message" : "OTP generated successfully",
  "data" : {
    "channel" : "email",
    "operationId" : "test_email_1777746568_1257",
    "sent" : true,
    "expiresAt" : "2026-05-02T18:44:30.166275046"
  },
  "error" : null
}
HTTP Code: 200
✓ ПРОЙДЕН: Генерация OTP (email)

  📧 Для просмотра Email откройте в браузере: http://localhost:8025

>>> ТЕСТ 11: Список пользователей (админ)
📤 ЗАПРОС: GET http://localhost:8080/api/admin/users (Authorization: Bearer ***)
📥 ОТВЕТ: {
  "success" : true,
  "message" : "Users retrieved",
  "data" : [ {
    "id" : 2,
    "username" : "user_1777746568_1257",
    "passwordHash" : "$2a$10$hpWJkoPk0xbzEMuO8c70s.R4JbR/uDFNiNrzFmkp6I6ixAgsS6N9W",
    "email" : "user_1777746568_1257@test.com",
    "phone" : "+78881257746568",
    "telegramChatId" : null,
    "role" : "USER",
    "createdAt" : "2026-05-02T18:29:29.710561",
    "updatedAt" : "2026-05-02T18:29:29.710561",
    "admin" : false
  } ],
  "error" : null
}
HTTP Code: 200
✓ ПРОЙДЕН: Список пользователей
  Всего пользователей: 1

>>> ТЕСТ 12: Неавторизованный доступ к админке
📤 ЗАПРОС: GET http://localhost:8080/api/admin/config (без авторизации)
📥 ОТВЕТ: {"success": false, "error": "Missing or invalid authorization header"}
HTTP Code: 401
✓ ПРОЙДЕН: Блокировка неавторизованного доступа

==========================================
ИТОГИ ТЕСТИРОВАНИЯ
==========================================\033[0m
Пройдено: 11
Не пройдено: 0

📌 Примечания:
  - Для просмотра отправленных Email откройте http://localhost:8025 (MailHog)
  - SMS сообщения сохраняются в логах сервиса
  - Уникальный ID запуска: 1777746568_1257
  - Администратор: admin_1777746568_1257 / admin123
  - Пользователь: user_1777746568_1257 / user123

🎉 ВСЕ ТЕСТЫ ПРОЙДЕНЫ! Сервис работает корректно



```

## 📝 Примеры запросов

### Health check

```bash
curl -s -X GET http://localhost:8080/api/health | jq .

```

**Ответ:**

```json
{
  "success": true,
  "message": "Service is running",
  "data": null,
  "error": null
}


```

### Логин

```bash

curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"123456"}'

```

**Ответ:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "message": "Login successful"
}

```

###  Регистрация администратора

```bash
ADMIN_USERNAME="admin_$(date +%s)_$$"
ADMIN_PASSWORD="admin123"
ADMIN_EMAIL="${ADMIN_USERNAME}@test.com"
ADMIN_PHONE="+7999${RANDOM}${RANDOM}"

echo "📝 Регистрация администратора: $ADMIN_USERNAME"

ADMIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$ADMIN_USERNAME\",
    \"password\": \"$ADMIN_PASSWORD\",
    \"email\": \"$ADMIN_EMAIL\",
    \"phone\": \"$ADMIN_PHONE\"
  }")

echo "$ADMIN_RESPONSE" | jq .

# Сохраняем токен
ADMIN_TOKEN=$(echo "$ADMIN_RESPONSE" | jq -r '.data.token')

if [ "$ADMIN_TOKEN" != "null" ] && [ -n "$ADMIN_TOKEN" ]; then
  echo "✅ Токен администратора получен"
else
  echo "❌ Ошибка получения токена"
  exit 1
fi

```

**Ответ:**

```json
📝 Регистрация администратора: admin_1777743283_3236
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "user": {
      "id": 3,
      "username": "admin_1777743283_3236",
      "passwordHash": "$2a$10$YZt2c8JWkbBH2Pb8aHvD0epbIshkysC5yiLFoQJ6tCN7.eyoeDFc.",
      "email": "admin_1777743283_3236@test.com",
      "phone": "+7999122651549",
      "telegramChatId": null,
      "role": "USER",
      "createdAt": "2026-05-02T17:34:43.410943",
      "updatedAt": "2026-05-02T17:34:43.410943",
      "admin": false
    },
    "token": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbl8xNzc3NzQzMjgzXzMyMzYiLCJ1c2VySWQiOjMsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzc3NzQzMjgzLCJleHAiOjE3Nzc4Mjk2ODN9.lPpnjpMC_uSdZLu1wNbQ7pLimeHnfVmn0pDVHf5bYu22tYKqMTnAjPDFzofyoe2u"
  },
  "error": null
}
✅ Токен администратора получен
```


###  Вход администратора (если уже зарегистрирован)

```bash
echo "🔑 Вход администратора..."

ADMIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$ADMIN_USERNAME\",
    \"password\": \"$ADMIN_PASSWORD\"
  }")

ADMIN_TOKEN=$(echo "$ADMIN_RESPONSE" | jq -r '.data.token')
echo "$ADMIN_RESPONSE" | jq .
echo "✅ Токен: ${ADMIN_TOKEN:0:50}..."


```

**Ответ:**

```json
🔑 Вход администратора...
{
  "success": true,
  "message": "Login successful",
  "data": {
    "user": {
      "id": 3,
      "username": "admin_1777743283_3236",
      "passwordHash": "$2a$10$YZt2c8JWkbBH2Pb8aHvD0epbIshkysC5yiLFoQJ6tCN7.eyoeDFc.",
      "email": "admin_1777743283_3236@test.com",
      "phone": "+7999122651549",
      "telegramChatId": null,
      "role": "USER",
      "createdAt": "2026-05-02T17:34:43.410943",
      "updatedAt": "2026-05-02T17:34:43.410943",
      "admin": false
    },
    "token": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbl8xNzc3NzQzMjgzXzMyMzYiLCJ1c2VySWQiOjMsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzc3NzQzNDI4LCJleHAiOjE3Nzc4Mjk4Mjh9.uSJcYjBkI0nV33_OVrIzq-Psugbz5-dFNB9wJJ8ZzV4SyBi0OR2fszpyoPXpZ8Xo"
  },
  "error": null
}
✅ Токен: eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbl8xNzc3NzQzM...


```

###  Получение конфигурации OTP (админ)

```bash
# Используйте те данные, которые уже работали
ADMIN_USERNAME="admin_1777742140_2588"
ADMIN_PASSWORD="admin123"

# Войдите как администратор
ADMIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$ADMIN_USERNAME\",\"password\":\"$ADMIN_PASSWORD\"}")

ADMIN_TOKEN=$(echo "$ADMIN_RESPONSE" | jq -r '.data.token')

echo "Токен администратора: $ADMIN_TOKEN"

# Теперь запрос 
curl -s -X GET http://localhost:8080/api/admin/config \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq .



```

**Ответ:**

```json
Токен администратора: eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbl8xNzc3NzQyMTQwXzI1ODgiLCJ1c2VySWQiOjEsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc3Nzc0MzYzOCwiZXhwIjoxNzc3ODMwMDM4fQ.z6HKpMeP3iG9nAx2R7YnzYhGp9ycf3EW7lNypCkFPXrsmjD8knwc6xp86a3NuOeX
{
  "success": true,
  "message": "Config retrieved",
  "data": {
    "id": 1,
    "ttlSeconds": 900,
    "codeLength": 8,
    "updatedAt": "2026-05-02T17:15:41.18194",
    "updatedBy": "admin_1777742140_2588"
  },
  "error": null
}


```


###  Обновление конфигурации OTP (админ)

```bash
# Используйте те данные, которые уже работали
ADMIN_USERNAME="admin_1777742140_2588"
ADMIN_PASSWORD="admin123"

# Войдите как администратор
ADMIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$ADMIN_USERNAME\",\"password\":\"$ADMIN_PASSWORD\"}")

ADMIN_TOKEN=$(echo "$ADMIN_RESPONSE" | jq -r '.data.token')

echo "Токен администратора: $ADMIN_TOKEN"

# Теперь запрос 
echo "⚙️ Обновление конфигурации OTP..."

curl -s -X PUT http://localhost:8080/api/admin/config \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "ttlSeconds": 900,
    "codeLength": 8
  }' | jq .



```

**Ответ:**

```json
Токен администратора: eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbl8xNzc3NzQyMTQwXzI1ODgiLCJ1c2VySWQiOjEsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc3Nzc0NDA2MywiZXhwIjoxNzc3ODMwNDYzfQ.6kuHlWnb0uc8v-Z9IocvkZ3w0BRqWvfBt_rJJBPOhpCLgP7qcmztriDR5eRJ6E4h
⚙ Обновление конфигурации OTP...
{
  "success": true,
  "message": "Config updated successfully",
  "data": {
    "id": 1,
    "ttlSeconds": 900,
    "codeLength": 8,
    "updatedAt": "2026-05-02T17:47:43.633306",
    "updatedBy": "admin_1777742140_2588"
  },
  "error": null
}


```

###  Регистрация обычного пользователя

```bash
USER_USERNAME="user_$(date +%s)_$$"
USER_PASSWORD="user123"
USER_EMAIL="${USER_USERNAME}@test.com"
USER_PHONE="+7888${RANDOM}${RANDOM}"

echo "👤 Регистрация пользователя: $USER_USERNAME"

USER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$USER_USERNAME\",
    \"password\": \"$USER_PASSWORD\",
    \"email\": \"$USER_EMAIL\",
    \"phone\": \"$USER_PHONE\"
  }")

echo "$USER_RESPONSE" | jq .
USER_TOKEN=$(echo "$USER_RESPONSE" | jq -r '.data.token')
echo "✅ Токен пользователя получен"




```

**Ответ:**

```json
USER_TOKEN=$(echo "$USER_RESPONSE" | jq -r '.data.token')
echo "✅ Токен пользователя получен"
👤 Регистрация пользователя: user_1777744129_3236
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "user": {
      "id": 4,
      "username": "user_1777744129_3236",
      "passwordHash": "$2a$10$y89lrXDlXampBkn/ADVXNuOfwwzjZF/pxhfsMScuO9Efi8/xuTvey",
      "email": "user_1777744129_3236@test.com",
      "phone": "+7888648717272",
      "telegramChatId": null,
      "role": "USER",
      "createdAt": "2026-05-02T17:48:49.912851",
      "updatedAt": "2026-05-02T17:48:49.912851",
      "admin": false
    },
    "token": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ1c2VyXzE3Nzc3NDQxMjlfMzIzNiIsInVzZXJJZCI6NCwicm9sZSI6IlVTRVIiLCJpYXQiOjE3Nzc3NDQxMjksImV4cCI6MTc3NzgzMDUyOX0.BxlIHKOlDmj-HJLtsfXJ7aIvjCAZHEVpzhO4hz6HaN3Hu3SuMGl08XE7XGj0Qc0d"
  },
  "error": null
}
✅ Токен пользователя получен


```

###  Вход обычного пользовател

```bash
echo "🔑 Вход пользователя..."

USER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$USER_USERNAME\",
    \"password\": \"$USER_PASSWORD\"
  }")

USER_TOKEN=$(echo "$USER_RESPONSE" | jq -r '.data.token')
echo "$USER_RESPONSE" | jq .
echo "✅ Токен: ${USER_TOKEN:0:50}..."



```

**Ответ:**

```json
🔑 Вход пользователя...
{
  "success": true,
  "message": "Login successful",
  "data": {
    "user": {
      "id": 4,
      "username": "user_1777744129_3236",
      "passwordHash": "$2a$10$y89lrXDlXampBkn/ADVXNuOfwwzjZF/pxhfsMScuO9Efi8/xuTvey",
      "email": "user_1777744129_3236@test.com",
      "phone": "+7888648717272",
      "telegramChatId": null,
      "role": "USER",
      "createdAt": "2026-05-02T17:48:49.912851",
      "updatedAt": "2026-05-02T17:48:49.912851",
      "admin": false
    },
    "token": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ1c2VyXzE3Nzc3NDQxMjlfMzIzNiIsInVzZXJJZCI6NCwicm9sZSI6IlVTRVIiLCJpYXQiOjE3Nzc3NDQzMTUsImV4cCI6MTc3NzgzMDcxNX0.evagOJP19GBvO1LuI46nz88JLj7Sz8G5pzVC2EZrc39SAiYK75Jb9O7OxDEFjGP0"
  },
  "error": null
}
✅ Токен: eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ1c2VyXzE3Nzc3NDQxM..


```

###  Генерация OTP (канал: file)

```bash
ADMIN_USERNAME="admin_1777742140_2588"
ADMIN_PASSWORD="admin123"

# Войдите как администратор
ADMIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$ADMIN_USERNAME\",\"password\":\"$ADMIN_PASSWORD\"}")

ADMIN_TOKEN=$(echo "$ADMIN_RESPONSE" | jq -r '.data.token')

echo "✅ Токен администратора получен: ${ADMIN_TOKEN:0:50}..."

# Генерация OTP (file) С ТОКЕНОМ
OPERATION_ID="test_file_$(date +%s)_$$"

echo "🔢 Генерация OTP (file) для operationId: $OPERATION_ID"

curl -s -X POST http://localhost:8080/api/otp/generate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d "{
    \"operationId\": \"$OPERATION_ID\",
    \"channel\": \"file\"
  }" | jq .




```

**Ответ:**

```json
✅ Токен администратора получен: eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbl8xNzc3NzQyM...
🔢 Генерация OTP (file) для operationId: test_file_1777744662_3236
{
  "success": true,
  "message": "OTP generated successfully",
  "data": {
    "channel": "file",
    "operationId": "test_file_1777744662_3236",
    "sent": true,
    "expiresAt": "2026-05-02T18:12:42.332639815"
  },
  "error": null
}



```

###  Генерация OTP (канал: sms)

```bash

# Используйте данные, которые уже работали
ADMIN_USERNAME="admin_1777742140_2588"
ADMIN_PASSWORD="admin123"

# Войдите как администратор
ADMIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$ADMIN_USERNAME\",\"password\":\"$ADMIN_PASSWORD\"}")

ADMIN_TOKEN=$(echo "$ADMIN_RESPONSE" | jq -r '.data.token')

echo "✅ Токен администратора получен: ${ADMIN_TOKEN:0:50}..."

# Генерация OTP (sms) С ПРАВИЛЬНЫМ ЗАГОЛОВКОМ
OPERATION_ID="test_sms_$(date +%s)_$$"

echo "📱 Генерация OTP (sms) для operationId: $OPERATION_ID"

curl -s -X POST http://localhost:8080/api/otp/generate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d "{
    \"operationId\": \"$OPERATION_ID\",
    \"channel\": \"sms\"
  }" | jq .


```

**Ответ:**

```json
✅ Токен администратора получен: eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbl8xNzc3NzQyM...
📱 Генерация OTP (sms) для operationId: test_sms_1777745172_3236
{
  "success": true,
  "message": "OTP generated successfully",
  "data": {
    "channel": "sms",
    "operationId": "test_sms_1777745172_3236",
    "sent": true,
    "expiresAt": "2026-05-02T18:21:12.833447248"
  },
  "error": null
}



```

###  Генерация OTP (канал: email)

```bash
# Сначала получим токен (если ещё не получили)
ADMIN_USERNAME="admin_1777742140_2588"
ADMIN_PASSWORD="admin123"

ADMIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$ADMIN_USERNAME\",\"password\":\"$ADMIN_PASSWORD\"}")

ADMIN_TOKEN=$(echo "$ADMIN_RESPONSE" | jq -r '.data.token')

echo "✅ Токен получен: ${ADMIN_TOKEN:0:50}..."

# Теперь ПРАВИЛЬНЫЙ запрос с авторизацией
OPERATION_ID="test_email_$(date +%s)_$$"

echo "📧 Генерация OTP (email) для operationId: $OPERATION_ID"

curl -s -X POST http://localhost:8080/api/otp/generate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d "{
    \"operationId\": \"$OPERATION_ID\",
    \"channel\": \"email\"
  }" | jq .

echo "📬 Для просмотра email откройте: http://localhost:8025"



```

**Ответ:**

```json
✅ Токен получен: eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbl8xNzc3NzQyM...
📧 Генерация OTP (email) для operationId: test_email_1777745551_3236
{
  "success": true,
  "message": "OTP generated successfully",
  "data": {
    "channel": "email",
    "operationId": "test_email_1777745551_3236",
    "sent": true,
    "expiresAt": "2026-05-02T18:27:31.506461666"
  },
  "error": null
}
📬 Для просмотра email откройте: http://localhost:8025



```
#### Тестирование OTP Service совместно с эмулятором telegram 

```bash

# 1. Удалить всех ботов
echo "Удаление всех существующих ботов..."
for id in $(curl -s http://localhost:3001/api/bots | jq -r '.[].id'); do
  echo "Удаление бота ID: $id"
  curl -s -X DELETE http://localhost:3001/api/bots/$id
  echo ""
done

# 2. Проверка что ботов нет
echo -e "\nТекущие боты после очистки:"
curl -s http://localhost:3001/api/bots | jq '.'

# 3. Создание нового бота
echo -e "\nСоздание нового бота..."
curl -X POST http://localhost:3001/api/bots \
  -H "Content-Type: application/json" \
  -d '{
    "name": "OTP Service Bot",
    "username": "otp_service_bot",
    "token": "1234567890:ABCdefGHIjklMNOpqrsTUVwxyz"
  }' | jq '.'

# 4. Проверка создания
echo -e "\nПроверка созданного бота:"
curl -s "http://localhost:3001/bot1234567890:ABCdefGHIjklMNOpqrsTUVwxyz/getMe" | jq '.'


echo "════════════════════════════════════════════════════════"
echo "     ТЕСТ ГЕНЕРАЦИИ OTP ЧЕРЕЗ TELEGRAM"
echo "════════════════════════════════════════════════════════"

# Создаем пользователя в эмуляторе
USER_RESP=$(curl -s -X POST http://localhost:3001/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"otp_user_'$(date +%s)'","first_name":"OTP","last_name":"User"}')
CHAT_ID=$(echo "$USER_RESP" | jq -r '.id')
echo "✅ CHAT_ID: $CHAT_ID"

# Регистрация в OTP сервисе
REG_RESP=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"tg_$(date +%s)\",\"password\":\"pass123\",\"email\":\"tg_$(date +%s)@test.com\",\"phone\":\"+79990000001\",\"telegramChatId\":\"$CHAT_ID\"}")
TOKEN=$(echo "$REG_RESP" | jq -r '.data.token')

# Генерация OTP через Telegram
echo -e "\n📤 Генерация OTP..."
curl -X POST http://localhost:8080/api/otp/generate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"operationId":"test_'$(date +%s)'","channel":"telegram"}' | jq '.'

# Показываем сообщение
sleep 2
echo -e "\n📨 Сообщение от бота:"
curl -s "http://localhost:3001/api/messages?user_id=$CHAT_ID" | jq '.[-1].text'

echo -e "\n✅ Готово!"

```

#### Очистка

```bash

echo "=== 1. Остановка и удаление старых контейнеров ==="
docker stop mailhog 2>/dev/null || true
docker rm mailhog 2>/dev/null || true
cd ~
cd otp-service/docker
docker compose down -v 2>/dev/null || true
cd ~
cd smpp-smsc-simulator
docker compose down -v 2>/dev/null || true

# Дополнительная очистка конфликтующих контейнеров
docker rm -f otp-postgres otp-service mailhog 2>/dev/null || true
docker rm -f smpp-smsc-simulator-smscsimulator-1 2>/dev/null || true
cd ~
cd  telegram-emulator
# Остановить и удалить контейнер
docker stop telegram-emulator
docker rm telegram-emulator

# Удалить старый образ
docker rmi telegram-emulator:latest

# Удалить все неиспользуемые образы и кэш
docker system prune -a -f

cd ~
rm -rf ~/telegram-emulator 2>/dev/null || true
rm -rf ~/.local/share/Trash/files/telegram-emulator* 2>/dev/null || true
rm -rf ~/otp-service 2>/dev/null || true
rm -rf ~/smpp-smsc-simulator 2>/dev/null || true


echo "=== 2. Удаление конфликтующих сетей ==="
docker network rm docker_otp-network 2>/dev/null || true
docker network rm smpp-smsc-simulator_default 2>/dev/null || true
docker network rm otp-network 2>/dev/null || true
docker network prune -f

echo "=== 3. Полная очистка Docker (образы, volumes, кэш) ==="
docker system prune -a --volumes -f

echo "=== 4. Удаление проблемного образа PostgreSQL (если есть) ==="
docker rmi postgres:latest postgres:15-alpine 2>/dev/null || true

```

#### Заключение

```
 Ключевые достижения

✅ Полнофункциональный OTP сервис - Генерация, валидация, управление статусами
✅ 4 канала доставки - File, SMS, Email, Telegram
✅ JWT безопасность - Токен-базированная аутентификация
✅ Ролевая модель - USER и ADMIN с разными правами
✅ Полная контейнеризация - Docker Compose с 5 сервисами
✅ Автоматическое тестирование - 12 сценариев coverage
✅ Production готовность - Логирование, пул соединений, graceful shutdown
✅ Гибкая конфигурация - Настройка TTL и длины кода на лету
✅ Самодостаточность - Включает эмуляторы всех внешних сервисов

```


