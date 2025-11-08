FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} discovery-service.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "discovery-service.jar"]
