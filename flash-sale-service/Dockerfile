FROM eclipse-temurin:17-jre-alpine-3.23
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} flash-sale-service.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "flash-sale-service.jar"]
