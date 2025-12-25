FROM eclipse-temurin:17-jre-slim
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} payment-service.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "payment-service.jar"]
