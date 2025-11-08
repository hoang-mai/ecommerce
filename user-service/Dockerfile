FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} user-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "user-service.jar"]
