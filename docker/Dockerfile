FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache tzdata curl wget
ENV TZ=UTC

RUN mkdir -p /app/config /app/logs /app/otp-files

COPY --from=build /app/target/otp-service-*.jar app.jar

# Копируем конфиги (если файлы есть - скопируются, если нет - проигнорируется)
COPY config/ /app/config/

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]






