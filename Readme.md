# Verteilte Systeme Praktikum
## Build/MVN/Docker
Mit dem Befehl `docker-compose up` kann das Projekt gestartet werden.
Um Container neu zu bauen, muss der Befehl `docker-compose build` ausgeführt werden.

Für beliebig viele Worker kann `docker-compose up --scale worker=X` ausgeführt werden, um X Worker zu starten.


RUN mkdir -p /app
COPY --from=build /app/target/Worker-1.0-SNAPSHOT.jar /app