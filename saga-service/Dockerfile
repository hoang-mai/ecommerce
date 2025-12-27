FROM eclipse-temurin:17-jre-alpine-3.23
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} saga-service.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "saga-service.jar"]
