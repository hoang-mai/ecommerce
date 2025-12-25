FROM eclipse-temurin:17-jre-slim
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} chat-notification-service.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "chat-notification-service.jar"]
