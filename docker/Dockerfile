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


# Создаем директории
RUN mkdir -p /app/config /app/logs /app/otp-files

# Копируем JAR
COPY --from=build /app/target/otp-service-*.jar app.jar

# Копируем конфиги из resources (если есть)
COPY src/main/resources/*.properties /app/config/ 2>/dev/null || true

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]






