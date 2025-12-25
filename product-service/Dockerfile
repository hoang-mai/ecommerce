FROM eclipse-temurin:17-jre-slim
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} product-service.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "product-service.jar"]
