FROM openjdk:8-jdk-alpine as BUILD

COPY . /kotlin-clinic-app
WORKDIR /kotlin-clinic-app
RUN ./gradlew --no-daemon shadowJar

FROM openjdk:8-jre-alpine

COPY --from=BUILD /kotlin-clinic-app/build/libs/kotlin-clinic-app-0.1.jar /bin/runner/run.jar
WORKDIR /bin/runner

CMD ["java","-jar","run.jar"]