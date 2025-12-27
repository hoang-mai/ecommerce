FROM eclipse-temurin:17-jre-alpine-3.23
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} user-service.jar

EXPOSE 8089

ENTRYPOINT ["java", "-jar", "user-service.jar"]
