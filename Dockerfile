# Stage 1: Build với Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

# Copy toàn bộ source (cần cả pom cha + module con)
COPY . .

ARG SERVICE_NAME=user-service

# Build module cụ thể
RUN mvn -pl ${SERVICE_NAME} -am clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy JAR từ stage build
ARG SERVICE_NAME=user-service
ENV SERVICE_NAME=${SERVICE_NAME}
COPY --from=builder /build/${SERVICE_NAME}/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
