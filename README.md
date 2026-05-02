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
| 📧 **Email рассылка** | Отправка кодов через SMTP |
| 📱 **SMS рассылка** | Отправка через SMPP эмулятор |
| 🤖 **Telegram рассылка** | Отправка через Telegram Bot API |
| 💾 **Сохранение в файл** | Альтернативный канал доставки |
| ⏰ **Автоматическое протухание** | Периодическая очистка просроченных кодов |
| 🐳 **Docker поддержка** | Контейнеризация приложения и базы данных |

---

## 🛠 Технологический стек

| Компонент | Технология |
|-----------|------------|
| **Язык** | Java 21 |
| **Сборка** | Maven |
| **База данных** | PostgreSQL 17 |
| **Доступ к БД** | JDBC + HikariCP |
| **API** | com.sun.net.httpserver |
| **Аутентификация** | JWT (jjwt) |
| **Хеширование** | BCrypt |
| **Email** | Jakarta Mail (Angus) |
| **SMS** | JSMPP |
| **Логирование** | Logback |
| **JSON** | Jackson |
| **Контейнеризация** | Docker + Docker Compose |

---

## 📁 Структура проекта

# Analytics Trainer — бэкенд-сервис для отработки навыков аналитик

## Описание проекта

Бэкенд-сервис для отработки практических навыков аналитиков в безопасной и контролируемой среде. Пользователи проходят задания разных типов, система фиксирует результаты, начисляет баллы и ведёт прогресс обучения



### Цели проекта

- Практическое применение навыков разработки бэкенд-сервисов
- Реализация REST API с аутентификацией и авторизацией
- Интеграция микросервисов (Java + Go)
- Работа с реляционными базами данных (PostgreSQL)
- Контейнеризация приложения с помощью Docker

### Реализованные функции

| № | Функция | Статус |
|---|---------|--------|
| 1 | Регистрация и аутентификация (JWT) | ✅ |
| 2 | Просмотр списка тренажёров | ✅ |
| 3 | Тестовые задания (один/несколько ответов) | ✅ |
| 4 | Задания на поиск ошибок | ✅ |
| 5 | Открытые задания | ✅ |
| 6 | Автоматическая проверка через Go-микросервис | ✅ |
| 7 | Начисление баллов по единым правилам | ✅ |
| 8 | Хранение и отображение прогресса | ✅

---

## Технологический стек

| Компонент | Технология | Версия |
|-----------|------------|--------|
| Основной бэкенд | Java + Javalin | 21 |
| Авто-чекер | Go + Fiber | 1.22.2 |
| База данных | PostgreSQL | 15 |
| Аутентификация | JWT + BCrypt | - |
| Контейнеризация | Docker + Docker Compose | 3.8 |
| Сборка | Maven | 3.8+ |

---
## Архитектура проекта

```
┌─────────────────────────────────────────────────┐
│                                                 │
│  Client ──► Java Backend (:8080)                │
│                  │                              │
│                  ├──► PostgreSQL (:5432)        │
│                  │                              │
│                  └──► Go Checker (:8081)        │
│                                                 │
└─────────────────────────────────────────────────┘

```
## Структура проекта

```
analytics-trainer/
├── build.sh # Сборка Java бэкенда
├── docker-compose.yml # Оркестрация трёх сервисов
├── final_check.sh # Финальная проверка
├── full_test.sh # Полное тестирование
├── README.md
├── run-tests.sh # Быстрое тестирование API
│
├── checker/ # АВТО-ЧЕКЕР (Go 1.22.2)
│ ├── Dockerfile
│ ├── go.mod
│ ├── go.sum
│ └── main.go # Fiber HTTP сервер
│
├── database/
│ └── init.sql # Схема БД + тестовые данные
│
└── java-backend/ # ОСНОВНОЙ СЕРВИС (Java 21)
├── Dockerfile
├── pom.xml # Maven конфигурация
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── com/analytics/trainer/
│ │ │ ├── Launcher.java # Точка входа
│ │ │ ├── config/
│ │ │ │ └── DatabaseConfig.java
│ │ │ ├── controller/
│ │ │ │ ├── AuthController.java
│ │ │ │ ├── ProgressController.java
│ │ │ │ └── TaskController.java
│ │ │ ├── dao/
│ │ │ │ ├── AttemptDao.java
│ │ │ │ ├── ProgressDao.java
│ │ │ │ ├── TaskDao.java
│ │ │ │ └── UserDao.java
│ │ │ ├── model/ # Java 21 Records
│ │ │ │ ├── Attempt.java
│ │ │ │ ├── Progress.java
│ │ │ │ ├── Task.java
│ │ │ │ └── User.java
│ │ │ ├── service/
│ │ │ │ ├── AuthService.java
│ │ │ │ ├── ProgressService.java
│ │ │ │ ├── ScoringService.java
│ │ │ │ └── TaskService.java
│ │ │ └── util/
│ │ │ ├── JwtUtil.java
│ │ │ └── PasswordUtil.java
│ │ └── resources/
│ └── test/
│ └── java/
└── target/ # Скомпилированные файлы (после сборки)
├── analytics-trainer-1.0.0.jar
├── analytics-trainer-1.0.0-jar-with-dependencies.jar
├── classes/ # Скомпилированные .class файлы
└── ...
```

---

## Структура базы данных

### Таблица users — пользователи системы

| Поле | Тип | Описание |
|------|-----|----------|
| id | SERIAL | Уникальный идентификатор (PK) |
| email | VARCHAR(255) | Email пользователя (UNIQUE) |
| password_hash | VARCHAR(255) | Хеш пароля (BCrypt) |
| full_name | VARCHAR(255) | Полное имя |
| role | VARCHAR(50) | Роль: student, admin |
| created_at | TIMESTAMP | Дата регистрации |

### Таблица tasks — задания

| Поле | Тип | Описание |
|------|-----|----------|
| id | SERIAL | Уникальный идентификатор (PK) |
| type | VARCHAR(50) | Тип: test, error_spotting, open |
| title | TEXT | Название задания |
| description | TEXT | Описание задания |
| content | JSONB | Содержимое задания |
| max_score | INTEGER | Максимальный балл (100) |
| created_at | TIMESTAMP | Дата создания |

**Структура JSONB для разных типов:**

| Тип задания | Поля JSON |
|-------------|-----------|
| test | `options` (массив), `correct` (массив индексов) |
| error_spotting | `broken_code` (строка), `expected_errors` (массив) |
| open | `hint` (строка), `auto_check` (boolean) |

### Таблица attempts — попытки выполнения

| Поле | Тип | Описание |
|------|-----|----------|
| id | SERIAL | Уникальный идентификатор (PK) |
| user_id | INTEGER | Ссылка на users(id) |
| task_id | INTEGER | Ссылка на tasks(id) |
| answer | JSONB | Ответ пользователя |
| score | INTEGER | Полученный балл |
| completed_at | TIMESTAMP | Время выполнения |

### Таблица user_progress — прогресс пользователя

| Поле | Тип | Описание |
|------|-----|----------|
| user_id | INTEGER | Ссылка на users(id) (PK) |
| total_points | INTEGER | Сумма баллов за все попытки |
| tasks_completed | INTEGER | Количество уникальных пройденных заданий |
| last_updated | TIMESTAMP | Время последнего обновления |

### Связи между таблицами

```
users (1) ──< attempts (N)
tasks (1) ──< attempts (N)
users (1) ──< user_progress (1)


```
## Тестовые данные (10 заданий)

| ID | Тип | Название | Max Score |
|----|-----|----------|-----------|
| 1 | test | SQL JOINs | 100 |
| 2 | test | Метрики продукта | 100 |
| 3 | test | Типы аналитики | 100 |
| 4 | error_spotting | Ошибки в SQL запросе | 100 |
| 5 | error_spotting | Ошибки в метриках | 100 |
| 6 | error_spotting | Ошибки в визуализации | 100 |
| 7 | open | Когортный анализ | 100 |
| 8 | open | A/B тестирование | 100 |
| 9 | open | RFM-анализ | 100 |
| 10 | open | Построение дашборда | 100 |

---

## API Endpoints

| Метод | Endpoint | Описание | Аутентификация |
|-------|----------|----------|----------------|
| POST | `/register` | Регистрация пользователя | Нет |
| POST | `/login` | Вход в систему | Нет |
| GET | `/trainings` | Список всех заданий | Да (JWT) |
| GET | `/trainings/{id}` | Детали задания | Да (JWT) |
| POST | `/trainings/{id}/attempt` | Отправить ответ | Да (JWT) |
| GET | `/profile/progress` | Прогресс пользователя | Да (JWT) |

---

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

# Клонирование репозитория
git clone https://github.com/AbyanovDamir/analytics-trainer.git
cd analytics-trainer

# Сборка Java бэкенда
sudo chmod +x build.sh
sudo ./build.sh

# Запуск контейнеров
docker compose up --build

# В другом терминале — тестирование
sudo chmod +x run-tests.sh
sudo ./run-tests.sh

# Полное тестирование
sudo chmod +x full_test.sh
sudo ./full_test.sh

# Финальная проверка
sudo chmod +x final_check.sh
sudo ./final_check.sh

```


#### Проверка работоспособности

```bash
# Проверка Go чекера
curl http://localhost:8081/health

# Регистрация и сохранение токена
REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"email":"final_user2@example.com","password":"123456","fullName":"Final User2"}')

TOKEN=$(echo $REGISTER_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Ваш токен: $TOKEN"

# Проверка списка заданий, используем сохранённый токен
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/trainings | jq '.[] | {id, title}'

# Отправить ответ
curl -s -X POST http://localhost:8080/trainings/1/attempt \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"answer":[0,1,2]}' | jq .

  # Проверить прогресс
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/profile/progress | jq .

```


#### Очистка

```bash
# Полная остановка и удаление
docker compose down -v
docker system prune -a -f

# Удалить все образы проекта
docker rmi $(docker images | grep analytics-trainer | awk '{print $3}') 2>/dev/null

# Пересобрать с нуля
docker compose up --build

```

#### Заключение

```
Проект реализует полностью работающий бэкенд для аналитического тренажёра с:

    ✅ JWT-аутентификацией

    ✅ Тремя типами заданий (тест, поиск ошибок, открытые)

    ✅ Автоматической проверкой через Go-микросервис

    ✅ Хранением прогресса в PostgreSQL

    ✅ Docker-оркестрацией (3 сервиса)

    ✅ Java 21 без Spring (Javalin)

    ✅ Go 1.22.2 (Fiber)

```


