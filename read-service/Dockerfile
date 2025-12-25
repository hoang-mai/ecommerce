FROM eclipse-temurin:17-jre-slim
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} read-service.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "read-service.jar"]
