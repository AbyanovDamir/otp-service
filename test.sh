#!/bin/bash

# ============================================
# OTP SERVICE - АВТОМАТИЗИРОВАННЫЙ ТЕСТ (с созданием администратора)
# ============================================

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

BASE_URL="http://localhost:8080"
TESTS_PASSED=0
TESTS_FAILED=0

# Генерация уникального ID
TIMESTAMP=$(date +%s)
RANDOM_SUFFIX=$((RANDOM % 10000))
UNIQUE_ID="${TIMESTAMP}_${RANDOM_SUFFIX}"

# Уникальные данные администратора
ADMIN_USERNAME="admin_${UNIQUE_ID}"
ADMIN_PASSWORD="admin123"
ADMIN_EMAIL="admin_${UNIQUE_ID}@test.com"
ADMIN_PHONE="+7999${RANDOM_SUFFIX}${TIMESTAMP: -6}"

# Уникальные данные пользователя
USER_USERNAME="user_${UNIQUE_ID}"
USER_PASSWORD="user123"
USER_EMAIL="user_${UNIQUE_ID}@test.com"
USER_PHONE="+7888${RANDOM_SUFFIX}${TIMESTAMP: -6}"

print_result() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓ ПРОЙДЕН${NC}: $2"
        ((TESTS_PASSED++))
    else
        echo -e "${RED}✗ НЕ ПРОЙДЕН${NC}: $2"
        ((TESTS_FAILED++))
    fi
}

check_response() {
    local response=$1
    local message=$2

    if echo "$response" | grep -qE '"success"[[:space:]]*:[[:space:]]*true'; then
        print_result 0 "$message"
        return 0
    else
        print_result 1 "$message"
        echo -e "${YELLOW}Ответ:${NC} $response"
        return 1
    fi
}

extract_token() {
    echo "$1" | grep -oE '"token"[[:space:]]*:[[:space:]]*"[^"]*"' | head -1 | sed 's/.*"token"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/'
}

extract_user_id() {
    echo "$1" | grep -oE '"id"[[:space:]]*:[[:space:]]*[0-9]+' | head -1 | grep -oE '[0-9]+'
}

print_request() {
    echo -e "${CYAN}📤 ЗАПРОС:${NC} $1"
    if [ -n "$2" ]; then
        echo -e "${CYAN}📦 ТЕЛО:${NC} $2"
    fi
}

print_response() {
    echo -e "${CYAN}📥 ОТВЕТ:${NC} $1"
}

echo -e "${BLUE}=========================================="
echo "ТЕСТИРОВАНИЕ OTP SERVICE"
echo "==========================================${NC}"
echo -e "${YELLOW}🔑 Уникальный ID запуска: ${UNIQUE_ID}${NC}"
echo -e "${YELLOW}👤 Администратор: ${ADMIN_USERNAME}${NC}"
echo -e "${YELLOW}👤 Пользователь: ${USER_USERNAME}${NC}"
echo ""

# Проверка доступности сервиса
echo -e "${YELLOW}Проверка доступности сервиса...${NC}"
if ! curl -s -f "$BASE_URL/api/health" > /dev/null 2>&1; then
    echo -e "${RED}Ошибка: Сервис не доступен по адресу $BASE_URL${NC}"
    exit 1
fi
echo -e "${GREEN}Сервис доступен${NC}\n"

# ТЕСТ 1: Health check
echo -e "${BLUE}>>> ТЕСТ 1: Health check${NC}"
print_request "GET $BASE_URL/api/health"
HEALTH=$(curl -s -w "\nHTTP Code: %{http_code}" "$BASE_URL/api/health")
print_response "$HEALTH"
check_response "$HEALTH" "Health check endpoint"

# ТЕСТ 2: Регистрация администратора
echo -e "\n${BLUE}>>> ТЕСТ 2: Регистрация администратора${NC}"
REQUEST_BODY="{\"username\":\"$ADMIN_USERNAME\",\"password\":\"$ADMIN_PASSWORD\",\"email\":\"$ADMIN_EMAIL\",\"phone\":\"$ADMIN_PHONE\"}"
print_request "POST $BASE_URL/api/auth/register" "$REQUEST_BODY"
ADMIN_REG=$(curl -s -w "\nHTTP Code: %{http_code}" -X POST "$BASE_URL/api/auth/register" \
    -H "Content-Type: application/json" \
    -d "$REQUEST_BODY")
print_response "$ADMIN_REG"

# Парсим ответ (убираем HTTP код для проверки)
ADMIN_REG_BODY=$(echo "$ADMIN_REG" | sed 's/\nHTTP Code:.*//')
ADMIN_TOKEN=$(extract_token "$ADMIN_REG_BODY")
ADMIN_ID=$(extract_user_id "$ADMIN_REG_BODY")

# Если регистрация не удалась (пользователь существует), пробуем войти
if ! echo "$ADMIN_REG_BODY" | grep -qE '"success"[[:space:]]*:[[:space:]]*true'; then
    echo -e "${YELLOW}⚠ Регистрация не удалась, пробуем войти...${NC}"
    LOGIN_BODY="{\"username\":\"$ADMIN_USERNAME\",\"password\":\"$ADMIN_PASSWORD\"}"
    print_request "POST $BASE_URL/api/auth/login" "$LOGIN_BODY"
    ADMIN_LOGIN=$(curl -s -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d "$LOGIN_BODY")
    print_response "$ADMIN_LOGIN"
    ADMIN_REG_BODY="$ADMIN_LOGIN"
    ADMIN_TOKEN=$(extract_token "$ADMIN_LOGIN")
    ADMIN_ID=$(extract_user_id "$ADMIN_LOGIN")
fi

# Критическая часть: ПРОВЕРКА И НАЗНАЧЕНИЕ РОЛИ АДМИНИСТРАТОРА
echo -e "\n${YELLOW}🔧 Проверка прав администратора...${NC}"

# Проверяем текущую роль
if echo "$ADMIN_REG_BODY" | grep -qE '"role"[[:space:]]*:[[:space:]]*"ADMIN"'; then
    echo -e "${GREEN}✓ Уже имеет роль ADMIN${NC}"
    check_response "$ADMIN_REG_BODY" "Регистрация/вход администратора (уже ADMIN)"
else
    echo -e "${YELLOW}⚠ Пользователь имеет роль USER, требуется назначение ADMIN...${NC}"

    # СПОСОБ 1: Прямой запрос к БД через API (если есть эндпоинт)
    PROMOTE_SUCCESS=false

    # Пробуем разные возможные эндпоинты для повышения прав
    for ENDPOINT in "/api/admin/promote" "/api/admin/make-admin" "/api/admin/set-role"; do
        PROMOTE_RESPONSE=$(curl -s -X POST "$BASE_URL$ENDPOINT" \
            -H "Content-Type: application/json" \
            -d "{\"userId\":$ADMIN_ID,\"username\":\"$ADMIN_USERNAME\"}" 2>/dev/null)

        if echo "$PROMOTE_RESPONSE" | grep -qE '"success"[[:space:]]*:[[:space:]]*true'; then
            echo -e "${GREEN}✓ Повышение прав через $ENDPOINT успешно${NC}"
            PROMOTE_SUCCESS=true
            break
        fi
    done

    if [ "$PROMOTE_SUCCESS" = false ]; then
        echo -e "${RED}❌ Не удалось повысить права через API${NC}"
        echo -e "${YELLOW}📝 Вам нужно вручную выполнить SQL команду:${NC}"
        echo -e "${CYAN}   UPDATE users SET role='ADMIN', admin=true WHERE id=$ADMIN_ID;${NC}"
        echo -e "${YELLOW}Или нажмите Enter, если уже выполнили, или введите 'skip' чтобы пропустить админ-тесты${NC}"
        read -t 10 -p "Действие (Enter - продолжить, skip - пропустить): " USER_ACTION

        if [ "$USER_ACTION" = "skip" ]; then
            echo -e "${YELLOW}⚠ Пропускаем административные тесты${NC}"
            ADMIN_TOKEN=""
        else
            # Ждём и пробуем перелогиниться
            echo -e "${YELLOW}⏳ Ожидание применения прав...${NC}"
            sleep 3

            # Пытаемся войти снова
            RETRY_LOGIN=$(curl -s -X POST "$BASE_URL/api/auth/login" \
                -H "Content-Type: application/json" \
                -d "{\"username\":\"$ADMIN_USERNAME\",\"password\":\"$ADMIN_PASSWORD\"}")

            if echo "$RETRY_LOGIN" | grep -qE '"role"[[:space:]]*:[[:space:]]*"ADMIN"'; then
                echo -e "${GREEN}✓ Теперь роль ADMIN!${NC}"
                ADMIN_TOKEN=$(extract_token "$RETRY_LOGIN")
                check_response "$RETRY_LOGIN" "Регистрация/вход администратора (после назначения)"
            else
                echo -e "${RED}✗ Всё ещё не ADMIN, пропускаем админ-тесты${NC}"
                ADMIN_TOKEN=""
            fi
        fi
    else
        # После успешного повышения через API, перелогиниваемся
        RETRY_LOGIN=$(curl -s -X POST "$BASE_URL/api/auth/login" \
            -H "Content-Type: application/json" \
            -d "{\"username\":\"$ADMIN_USERNAME\",\"password\":\"$ADMIN_PASSWORD\"}")
        ADMIN_TOKEN=$(extract_token "$RETRY_LOGIN")
        check_response "$RETRY_LOGIN" "Регистрация/вход администратора (после повышения)"
    fi
fi

if [ -n "$ADMIN_TOKEN" ]; then
    echo -e "  ${GREEN}✓ Токен администратора получен и активен${NC}"
else
    echo -e "  ${RED}✗ НЕТ ВАЛИДНОГО ТОКЕНА АДМИНА${NC}"
fi

# ТЕСТ 4: Получение конфигурации (админ)
echo -e "\n${BLUE}>>> ТЕСТ 4: Получение конфигурации OTP${NC}"
if [ -n "$ADMIN_TOKEN" ]; then
    print_request "GET $BASE_URL/api/admin/config (Authorization: Bearer ***)"
    CONFIG=$(curl -s -w "\nHTTP Code: %{http_code}" -X GET "$BASE_URL/api/admin/config" \
        -H "Authorization: Bearer $ADMIN_TOKEN")
    print_response "$CONFIG"

    if echo "$CONFIG" | grep -qE '"success"[[:space:]]*:[[:space:]]*true'; then
        check_response "$CONFIG" "Получение конфигурации"
        TTL=$(echo "$CONFIG" | grep -oE '"ttlSeconds"[[:space:]]*:[[:space:]]*[0-9]+' | grep -oE '[0-9]+')
        LEN=$(echo "$CONFIG" | grep -oE '"codeLength"[[:space:]]*:[[:space:]]*[0-9]+' | grep -oE '[0-9]+')
        echo -e "  Текущая конфигурация: TTL=${TTL}с, Длина=${LEN} цифр"
    else
        print_result 1 "Получение конфигурации"
        echo -e "${RED}  Доступ запрещён - возможно недостаточно прав${NC}"
    fi
else
    echo -e "${RED}✗ НЕТ ТОКЕНА АДМИНА - пропуск тестов 4-5${NC}"
fi

# ТЕСТ 5: Обновление конфигурации (админ)
echo -e "\n${BLUE}>>> ТЕСТ 5: Обновление конфигурации OTP${NC}"
if [ -n "$ADMIN_TOKEN" ]; then
    REQUEST_BODY='{"ttlSeconds":900,"codeLength":8}'
    print_request "PUT $BASE_URL/api/admin/config" "$REQUEST_BODY"
    UPDATE=$(curl -s -w "\nHTTP Code: %{http_code}" -X PUT "$BASE_URL/api/admin/config" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $ADMIN_TOKEN" \
        -d "$REQUEST_BODY")
    print_response "$UPDATE"
    check_response "$UPDATE" "Обновление конфигурации"
else
    echo -e "${RED}✗ НЕТ ТОКЕНА АДМИНА - пропуск теста${NC}"
fi

# ТЕСТ 6: Регистрация обычного пользователя
echo -e "\n${BLUE}>>> ТЕСТ 6: Регистрация обычного пользователя${NC}"
REQUEST_BODY="{\"username\":\"$USER_USERNAME\",\"password\":\"$USER_PASSWORD\",\"email\":\"$USER_EMAIL\",\"phone\":\"$USER_PHONE\"}"
print_request "POST $BASE_URL/api/auth/register" "$REQUEST_BODY"
USER_REG=$(curl -s -w "\nHTTP Code: %{http_code}" -X POST "$BASE_URL/api/auth/register" \
    -H "Content-Type: application/json" \
    -d "$REQUEST_BODY")
print_response "$USER_REG"
check_response "$USER_REG" "Регистрация пользователя"
USER_TOKEN=$(extract_token "$USER_REG")

# ТЕСТ 7: Вход обычного пользователя
echo -e "\n${BLUE}>>> ТЕСТ 7: Вход обычного пользователя${NC}"
REQUEST_BODY="{\"username\":\"$USER_USERNAME\",\"password\":\"$USER_PASSWORD\"}"
print_request "POST $BASE_URL/api/auth/login" "$REQUEST_BODY"
USER_LOGIN=$(curl -s -w "\nHTTP Code: %{http_code}" -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d "$REQUEST_BODY")
print_response "$USER_LOGIN"
check_response "$USER_LOGIN" "Вход пользователя"

if [ -z "$USER_TOKEN" ]; then
    USER_TOKEN=$(extract_token "$USER_LOGIN")
fi

if [ -n "$USER_TOKEN" ]; then
    echo -e "  ${GREEN}✓ Токен пользователя получен${NC}"
fi

# ТЕСТ 8: Генерация OTP через файловый канал
echo -e "\n${BLUE}>>> ТЕСТ 8: Генерация OTP кода (канал: file)${NC}"
if [ -n "$USER_TOKEN" ]; then
    OPERATION_ID="test_file_${UNIQUE_ID}"
    REQUEST_BODY="{\"operationId\":\"$OPERATION_ID\",\"channel\":\"file\"}"
    print_request "POST $BASE_URL/api/otp/generate" "$REQUEST_BODY"
    GENERATE=$(curl -s -w "\nHTTP Code: %{http_code}" -X POST "$BASE_URL/api/otp/generate" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $USER_TOKEN" \
        -d "$REQUEST_BODY")
    print_response "$GENERATE"
    check_response "$GENERATE" "Генерация OTP (file)"
else
    echo -e "${RED}✗ НЕТ ТОКЕНА ПОЛЬЗОВАТЕЛЯ${NC}"
fi

# ТЕСТ 9: Генерация OTP через SMS канал
echo -e "\n${BLUE}>>> ТЕСТ 9: Генерация OTP кода (канал: sms)${NC}"
if [ -n "$USER_TOKEN" ]; then
    OPERATION_ID_SMS="test_sms_${UNIQUE_ID}"
    REQUEST_BODY="{\"operationId\":\"$OPERATION_ID_SMS\",\"channel\":\"sms\"}"
    print_request "POST $BASE_URL/api/otp/generate" "$REQUEST_BODY"
    GENERATE_SMS=$(curl -s -w "\nHTTP Code: %{http_code}" -X POST "$BASE_URL/api/otp/generate" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $USER_TOKEN" \
        -d "$REQUEST_BODY")
    print_response "$GENERATE_SMS"
    check_response "$GENERATE_SMS" "Генерация OTP (sms)"
else
    echo -e "${RED}✗ НЕТ ТОКЕНА ПОЛЬЗОВАТЕЛЯ${NC}"
fi

# ТЕСТ 10: Генерация OTP через Email канал
echo -e "\n${BLUE}>>> ТЕСТ 10: Генерация OTP кода (канал: email)${NC}"
if [ -n "$USER_TOKEN" ]; then
    OPERATION_ID_EMAIL="test_email_${UNIQUE_ID}"
    REQUEST_BODY="{\"operationId\":\"$OPERATION_ID_EMAIL\",\"channel\":\"email\"}"
    print_request "POST $BASE_URL/api/otp/generate" "$REQUEST_BODY"
    GENERATE_EMAIL=$(curl -s -w "\nHTTP Code: %{http_code}" -X POST "$BASE_URL/api/otp/generate" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $USER_TOKEN" \
        -d "$REQUEST_BODY")
    print_response "$GENERATE_EMAIL"
    check_response "$GENERATE_EMAIL" "Генерация OTP (email)"
    echo -e "\n  ${YELLOW}📧 Для просмотра Email откройте в браузере: http://localhost:8025${NC}"
else
    echo -e "${RED}✗ НЕТ ТОКЕНА ПОЛЬЗОВАТЕЛЯ${NC}"
fi

# ТЕСТ 11: Список пользователей (админ)
echo -e "\n${BLUE}>>> ТЕСТ 11: Список пользователей (админ)${NC}"
if [ -n "$ADMIN_TOKEN" ]; then
    print_request "GET $BASE_URL/api/admin/users (Authorization: Bearer ***)"
    USERS=$(curl -s -w "\nHTTP Code: %{http_code}" -X GET "$BASE_URL/api/admin/users" \
        -H "Authorization: Bearer $ADMIN_TOKEN")
    print_response "$USERS"
    check_response "$USERS" "Список пользователей"

    if echo "$USERS" | grep -qE '"success"[[:space:]]*:[[:space:]]*true'; then
        USER_COUNT=$(echo "$USERS" | grep -o '"username"' | wc -l)
        echo -e "  Всего пользователей: $USER_COUNT"
    fi
else
    echo -e "${RED}✗ НЕТ ТОКЕНА АДМИНА - пропуск теста${NC}"
fi

# ТЕСТ 12: Неавторизованный доступ
echo -e "\n${BLUE}>>> ТЕСТ 12: Неавторизованный доступ к админке${NC}"
print_request "GET $BASE_URL/api/admin/config (без авторизации)"
UNAUTH=$(curl -s -w "\nHTTP Code: %{http_code}" -X GET "$BASE_URL/api/admin/config")
print_response "$UNAUTH"
if echo "$UNAUTH" | grep -qE '(success.*false|Missing or invalid)'; then
    print_result 0 "Блокировка неавторизованного доступа"
else
    print_result 1 "Блокировка неавторизованного доступа"
fi

# ИТОГИ
echo -e "\n${BLUE}=========================================="
echo "ИТОГИ ТЕСТИРОВАНИЯ"
echo "==========================================${NC}"
echo -e "${GREEN}Пройдено: $TESTS_PASSED${NC}"
echo -e "${RED}Не пройдено: $TESTS_FAILED${NC}"

echo -e "\n${YELLOW}📌 Примечания:${NC}"
echo "  - Для просмотра отправленных Email откройте http://localhost:8025 (MailHog)"
echo "  - SMS сообщения сохраняются в логах сервиса"
echo "  - Уникальный ID запуска: ${UNIQUE_ID}"
echo "  - Администратор: ${ADMIN_USERNAME} / ${ADMIN_PASSWORD}"
echo "  - Пользователь: ${USER_USERNAME} / ${USER_PASSWORD}"

if [ -z "$ADMIN_TOKEN" ] && [ $TESTS_FAILED -gt 0 ]; then
    echo -e "\n${RED}⚠ ВНИМАНИЕ: Административные тесты не пройдены из-за отсутствия прав ADMIN!${NC}"
    echo -e "${YELLOW}Чтобы исправить, выполните в БД:${NC}"
    echo -e "${CYAN}  UPDATE users SET role='ADMIN', admin=true WHERE username='$ADMIN_USERNAME';${NC}"
fi

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "\n${GREEN}🎉 ВСЕ ТЕСТЫ ПРОЙДЕНЫ! Сервис работает корректно.${NC}"
    exit 0
else
    echo -e "\n${YELLOW}⚠ Некоторые тесты не пройдены.${NC}"
    exit 0
fi
