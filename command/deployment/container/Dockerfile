FROM openjdk:17-slim

WORKDIR /app

COPY command/build/libs/cqrs-command.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
