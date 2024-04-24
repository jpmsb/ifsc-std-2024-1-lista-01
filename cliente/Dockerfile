FROM eclipse-temurin:17-jdk-alpine as builder

WORKDIR /cliente

COPY cliente /cliente

RUN ./gradlew installDist

FROM eclipse-temurin:17-jre-alpine

COPY --from=builder /cliente/app/build/libs/app.jar /cliente.jar

ENTRYPOINT java -jar /cliente.jar
