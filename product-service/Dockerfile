FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} product-service.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "product-service.jar"]
