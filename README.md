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

## 📝 Примеры запросов

### Регистрация
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"123456","email":"user@test.com","phone":"+79991234567"}'




## Примеры запросов и ответов

### Регистрация

```bash

curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"123456","fullName":"User Name"}'

```

**Ответ:**

```json

{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "message": "Registration successful"
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

###  Получение списка заданий

```bash

curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/trainings

```

**Ответ:**

```json
[
  {
    "id": 1,
    "type": "test",
    "title": "SQL JOINs",
    "description": "Выберите правильные варианты",
    "maxScore": 100
  }
]

```


###  Отправка ответа на тестовое задание

```bash

curl -X POST http://localhost:8080/trainings/1/attempt \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"answer":[0,1,2]}'


```

**Ответ:**

```json
{
  "score": 100,
  "max_score": 100,
  "message": "Attempt submitted successfully"
}

```

###  Проверка прогресса

```bash

curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/profile/progress


```

**Ответ:**

```json
{
  "total_points": 100,
  "tasks_completed": 1
}

```
---
### Запуск проекта и тестирование
#### Требования

- Docker (20.10+), Docker Compose (2.0+)
- Java 21 (для сборки)
- Maven 3.8+

#### Установка и запуск

```bash

echo "=== 5. Создание общей сети ==="
docker network create otp-network

echo "=== 6. Запуск MailHog ==="
docker run -d -p 1025:1025 -p 8025:8025 --name mailhog --network otp-network mailhog/mailhog


git clone https://github.com/melroselabs/smpp-smsc-simulator.git
echo "=== 7. Запуск SMPP симулятора ==="
cd smpp-smsc-simulator
docker compose down
docker compose up -d
docker network connect otp-network smpp-smsc-simulator-smscsimulator-1

cd telegram-emulator

#3. Создание Dockerfile

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


#8. Проверка работы


docker ps | grep telegram-emulator
docker logs telegram-emulator

#9. Тестирование API


# Получить пользователей
curl -s http://localhost:3001/api/users | head -c 500

# Получить чаты
curl -s http://localhost:3001/api/chats | head -c 500


git clone https://github.com/AbyanovDamir/otp-service.git
cd otp-mvn clean package -DskipTests -q
echo "=== 10. Запуск всех сервисов ==="
cd /home/damir/otp/otp5/otp-service/docker
docker compose up -d --buildservice

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
if curl -s http://localhost:8080/health > /dev/null; then
    echo "✅ OTP сервис доступен"
    curl -s http://localhost:8080/health | head -1
else
    echo "⚠️ OTP сервис не отвечает, проверьте логи:"
    docker logs otp-service --tail 30 2>/dev/null || echo "Контейнер OTP сервиса не найден"
fi


```
#### В другом терминале — тестирование
```bash
sudo chmod +x run-tests.sh
sudo ./run-tests.sh

```

#### Очистка

```bash

echo "=== 1. Остановка и удаление старых контейнеров ==="
docker stop mailhog 2>/dev/null || true
docker rm mailhog 2>/dev/null || true

cd /home/damir/otp/otp5/otp-service/docker
docker compose down -v 2>/dev/null || true

cd /home/damir/smpp-smsc-simulator
docker compose down -v 2>/dev/null || true

# Дополнительная очистка конфликтующих контейнеров
docker rm -f otp-postgres otp-service mailhog 2>/dev/null || true
docker rm -f smpp-smsc-simulator-smscsimulator-1 2>/dev/null || true

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


