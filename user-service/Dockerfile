FROM eclipse-temurin:17-jre-slim
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} user-service.jar

EXPOSE 8089

ENTRYPOINT ["java", "-jar", "user-service.jar"]
