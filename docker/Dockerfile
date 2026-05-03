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

# Копируем конфиги через RUN shell
RUN mkdir -p /app/config && \
    if [ -d src/main/resources ]; then \
        cp src/main/resources/*.properties /app/config/ 2>/dev/null || true; \
    fi

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]





