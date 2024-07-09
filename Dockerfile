FROM sbtscala/scala-sbt:eclipse-temurin-alpine-22_36_1.10.1_3.4.2

WORKDIR /app

COPY build.sbt /app/
COPY project /app/project

COPY . /app

RUN sbt compile

EXPOSE 8080

CMD ["sbt", "run"]
