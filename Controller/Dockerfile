##
## Build stage
##
FROM maven:3.8.1-openjdk-11-slim AS build

RUN mkdir -p /app
COPY target/Controller-1.0-SNAPSHOT.jar /app/

##
## Run stage
##
FROM openjdk:11.0-jre-slim

CMD ["java", "-jar", "/app/Controller-1.0-SNAPSHOT.jar"]