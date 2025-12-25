FROM eclipse-temurin:17-jre-slim
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} auth-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "auth-service.jar"]
