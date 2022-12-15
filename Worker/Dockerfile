##
## Build stage
##
FROM maven:3.8.1-openjdk-11-slim AS build
EXPOSE 6543/udp

RUN mkdir -p /app
COPY target/Worker-1.0-SNAPSHOT.jar /app/

##
## Run stage
##
FROM openjdk:11.0-jre-slim

CMD ["java", "-jar", "/app/Worker-1.0-SNAPSHOT.jar"]