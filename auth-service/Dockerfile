FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} auth-service.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "auth-service.jar"]
