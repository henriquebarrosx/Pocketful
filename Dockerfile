FROM gradle:7.5.0-jdk17 AS build

WORKDIR /home/gradle/project

COPY . .

RUN ./gradlew build --scan

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
