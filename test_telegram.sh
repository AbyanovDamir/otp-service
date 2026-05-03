#!/bin/bash

echo "═══════════════════════════════════════════════════════════════════════════"
echo "                     РАБОЧИЙ ТЕСТ OTP SERVICE"
echo "═══════════════════════════════════════════════════════════════════════════"

# Конфигурация
OTP_API="http://localhost:8080"
TELEGRAM_API="http://localhost:3001"
BOT_TOKEN="1234567890:ABCdefGHIjklMNOpqrsTUVwxyz"

# Цвета
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

PASSED=0
FAILED=0

# 1. Создаем пользователя в Telegram
echo -e "\n${BLUE}1. СОЗДАНИЕ ПОЛЬЗОВАТЕЛЯ В TELEGRAM${NC}"
TG_RESP=$(curl -s -X POST ${TELEGRAM_API}/api/users \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"user_$(date +%s)\",\"first_name\":\"Test\"}")

CHAT_ID=$(echo "$TG_RESP" | jq -r '.user.id')
echo "CHAT_ID: $CHAT_ID"

if [ "$CHAT_ID" != "null" ] && [ -n "$CHAT_ID" ]; then
    echo -e "${GREEN}✅ Пользователь создан${NC}"
    ((PASSED++))
else
    echo -e "${RED}❌ Ошибка создания${NC}"
    ((FAILED++))
fi

# 2. Регистрация в OTP сервисе
echo -e "\n${BLUE}2. РЕГИСТРАЦИЯ В OTP СЕРВИСЕ${NC}"
TIMESTAMP=$(date +%s)
REG_RESP=$(curl -s -X POST ${OTP_API}/api/auth/register \
    -H "Content-Type: application/json" \
    -d "{
        \"username\": \"test_${TIMESTAMP}\",
        \"password\": \"pass123\",
        \"email\": \"test_${TIMESTAMP}@test.com\"
    }")

TOKEN=$(echo "$REG_RESP" | jq -r '.data.token')
USER_ID=$(echo "$REG_RESP" | jq -r '.data.user.id')
echo "USER_ID: $USER_ID"

if [ "$TOKEN" != "null" ] && [ -n "$TOKEN" ]; then
    echo -e "${GREEN}✅ Регистрация успешна${NC}"
    ((PASSED++))
else
    echo -e "${RED}❌ Ошибка регистрации${NC}"
    ((FAILED++))
fi

# 3. Привязка Chat ID
echo -e "\n${BLUE}3. ПРИВЯЗКА CHAT ID${NC}"
docker exec otp-postgres psql -U otp_user -d otp_service -c \
    "UPDATE users SET telegram_chat_id = '${CHAT_ID}' WHERE id = ${USER_ID};" > /dev/null 2>&1

CHECK=$(docker exec otp-postgres psql -U otp_user -d otp_service -t -c \
    "SELECT telegram_chat_id FROM users WHERE id = ${USER_ID};" 2>/dev/null | tr -d ' ')

if [ "$CHECK" = "$CHAT_ID" ]; then
    echo -e "${GREEN}✅ Chat ID привязан${NC}"
    ((PASSED++))
else
    echo -e "${RED}❌ Ошибка привязки${NC}"
    ((FAILED++))
fi

# 4. Генерация OTP
echo -e "\n${BLUE}4. ГЕНЕРАЦИЯ OTP${NC}"
OPERATION_ID="test_$(date +%s)"
OTP_RESP=$(curl -s -X POST ${OTP_API}/api/otp/generate \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d "{\"operationId\": \"$OPERATION_ID\", \"channel\": \"telegram\"}")

SENT=$(echo "$OTP_RESP" | jq -r '.data.sent')
echo "$OTP_RESP" | jq '.'

if [ "$SENT" = "true" ]; then
    echo -e "${GREEN}✅ OTP отправлен${NC}"
    ((PASSED++))
else
    echo -e "${RED}❌ OTP не отправлен${NC}"
    ((FAILED++))
fi

# 5. Получение OTP кода из чатов (вместо getUpdates)
echo -e "\n${BLUE}5. ПОЛУЧЕНИЕ OTP КОДА ИЗ ЧАТОВ${NC}"
echo "Ожидание сообщения..."

OTP_CODE=""
MAX_ATTEMPTS=15
ATTEMPT=1

while [ $ATTEMPT -le $MAX_ATTEMPTS ] && [ -z "$OTP_CODE" ]; do
    sleep 1
    echo -n "  Попытка $ATTEMPT/$MAX_ATTEMPTS... "
    
    # Получаем чаты и ищем последнее сообщение для нашего CHAT_ID
    CHATS=$(curl -s "${TELEGRAM_API}/api/chats")
    
    # Ищем чат с нужным ID и извлекаем OTP код из last_message.text
    MESSAGE=$(echo "$CHATS" | jq -r ".chats[] | select(.id == $CHAT_ID) | .last_message.text // empty")
    
    if [ -n "$MESSAGE" ]; then
        OTP_CODE=$(echo "$MESSAGE" | grep -oE '[0-9]{6,8}' | head -1)
    fi
    
    if [ -n "$OTP_CODE" ]; then
        echo -e "${GREEN}НАЙДЕН!${NC}"
        echo "Сообщение: $MESSAGE"
    else
        echo -n "нет"
        if [ -n "$MESSAGE" ]; then
            echo " (пришло: $MESSAGE)"
        else
            echo ""
        fi
    fi
    
    ATTEMPT=$((ATTEMPT + 1))
done

if [ -n "$OTP_CODE" ]; then
    echo -e "${GREEN}✅ OTP код получен: $OTP_CODE${NC}"
    ((PASSED++))
else
    echo -e "${RED}❌ OTP код не получен${NC}"
    ((FAILED++))
fi

# 6. Валидация OTP
if [ -n "$OTP_CODE" ]; then
    echo -e "\n${BLUE}6. ВАЛИДАЦИЯ OTP${NC}"
    VALIDATE_RESP=$(curl -s -X POST ${OTP_API}/api/otp/validate \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d "{\"operationId\": \"$OPERATION_ID\", \"code\": \"$OTP_CODE\"}")
    
    echo "$VALIDATE_RESP" | jq '.'
    VALID=$(echo "$VALIDATE_RESP" | jq -r '.success')
    
    if [ "$VALID" = "true" ]; then
        echo -e "${GREEN}✅ OTP валидирован успешно!${NC}"
        ((PASSED++))
    else
        echo -e "${RED}❌ Ошибка валидации${NC}"
        ((FAILED++))
    fi
else
    echo -e "\n${YELLOW}⚠️ Пропуск валидации - OTP код не получен${NC}"
fi

# 7. Проверка статуса в БД
echo -e "\n${BLUE}7. ПРОВЕРКА СТАТУСА В БД${NC}"
STATUS=$(docker exec otp-postgres psql -U otp_user -d otp_service -t -c \
    "SELECT status FROM otp_codes WHERE operation_id = '$OPERATION_ID';" 2>/dev/null | tr -d ' ')

echo "Статус OTP: $STATUS"
if [ "$STATUS" = "USED" ]; then
    echo -e "${GREEN}✅ Статус корректный (USED)${NC}"
    ((PASSED++))
elif [ "$STATUS" = "ACTIVE" ]; then
    echo -e "${YELLOW}⚠️ Статус ACTIVE (еще не использован)${NC}"
else
    echo -e "${RED}❌ Статус: $STATUS${NC}"
    ((FAILED++))
fi

# ИТОГИ
echo -e "\n═══════════════════════════════════════════════════════════════════════════"
echo -e "${BLUE}ИТОГИ ТЕСТИРОВАНИЯ${NC}"
echo "═══════════════════════════════════════════════════════════════════════════"
echo -e "✅ Успешно: ${GREEN}$PASSED${NC}"
echo -e "❌ Ошибок: ${RED}$FAILED${NC}"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 ВСЕ ТЕСТЫ ПРОЙДЕНЫ! 🎉${NC}"
    echo ""
    echo "📝 ДАННЫЕ ДЛЯ ВХОДА:"
    echo "   Username: test_${TIMESTAMP}"
    echo "   Password: pass123"
    echo "   OTP Code: $OTP_CODE"
    echo "   Operation ID: $OPERATION_ID"
elif [ $PASSED -gt 0 ]; then
    echo -e "${YELLOW}⚠️ Тест завершен с ошибками (${FAILED} из $((PASSED+FAILED)))${NC}"
fi

echo "═══════════════════════════════════════════════════════════════════════════"

