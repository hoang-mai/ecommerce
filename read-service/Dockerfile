FROM eclipse-temurin:17-jre-alpine-3.23
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} read-service.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "read-service.jar"]
