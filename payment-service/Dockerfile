FROM eclipse-temurin:17-jre-alpine-3.23
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} payment-service.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "payment-service.jar"]
