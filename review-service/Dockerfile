FROM eclipse-temurin:17-jre-alpine-3.23
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} review-service.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "review-service.jar"]
