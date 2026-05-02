FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Устанавливаем утилиты
RUN apk add --no-cache tzdata curl wget
ENV TZ=UTC

# Копируем JAR
COPY --from=build /app/target/otp-service-*.jar app.jar

# Копируем конфигурационные файлы
COPY src/main/resources/*.properties /app/config/

# Создаем директории для логов и файлов
RUN mkdir -p /app/logs /app/otp-files

# Открываем порт
EXPOSE 8080

# Запускаем приложение с указанием внешней конфигурации
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=/app/config/"]