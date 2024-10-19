FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/survey-app-0.0.1.jar
COPY ${JAR_FILE} survey-app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/survey-app.jar"]