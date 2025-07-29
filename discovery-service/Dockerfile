FROM openjdk:17

ARG JAR_FILE=target/*.jar

ADD ${JAR_FILE} discovery-service.jar


EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/discovery-service.jar"]