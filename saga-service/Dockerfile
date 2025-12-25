FROM eclipse-temurin:17-jre-slim
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} saga-service.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "saga-service.jar"]
