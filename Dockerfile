FROM stakater/gradle:3.5-alpine

COPY src /app/src
COPY build.gradle /app/build.gradle
COPY gradle.properties /app/gradle.properties

WORKDIR /app

RUN gradle clean test

RUN gradle installDist

RUN ls build/install

ENTRYPOINT ./build/install/app/bin/app