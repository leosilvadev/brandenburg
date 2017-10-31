FROM stakater/gradle:3.5-alpine

COPY src /app/src
COPY build.gradle /app/build.gradle
COPY gradle.properties /app/gradle.properties

WORKDIR /app

CMD gradle clean test

CMD gradle installDist

ENTRYPOINT ./build/install/app/bin/app