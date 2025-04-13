FROM openjdk:17-slim

WORKDIR /app

COPY query/build/libs/cqrs-query.jar /app/app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
