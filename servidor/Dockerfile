FROM eclipse-temurin:17-jdk-alpine as builder

WORKDIR /servidor

COPY servidor /servidor

RUN ./gradlew installDist

FROM eclipse-temurin:17-jre-alpine

COPY --from=builder /servidor/app/build/libs/app.jar /servidor.jar

ENTRYPOINT java -jar /servidor.jar
