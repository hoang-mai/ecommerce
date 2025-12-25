FROM eclipse-temurin:17-jre-slim
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} review-service.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "review-service.jar"]
