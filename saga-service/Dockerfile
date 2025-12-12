FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} saga-service.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "saga-service.jar"]
