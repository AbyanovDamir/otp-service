.PHONY: build run docker-up docker-down docker-logs clean test

build:
	mvn clean package

run:
	mvn clean package
	java -jar target/otp-service-1.0.0.jar

docker-up:
	cd docker && docker compose up -d

docker-down:
	cd docker && docker compose down

docker-down-v:
	cd docker && docker compose down -v

docker-logs:
	cd docker && docker compose logs -f

docker-logs-otp:
	cd docker && docker compose logs -f otp-service

clean:
	mvn clean
	rm -rf logs/
