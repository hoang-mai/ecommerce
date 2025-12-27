FROM eclipse-temurin:17-jre-alpine-3.23

WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} order-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "order-service.jar"]

