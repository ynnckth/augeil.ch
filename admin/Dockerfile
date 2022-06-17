FROM maven:3.8.5-jdk-11-slim AS build
RUN mkdir /project
COPY . /project
WORKDIR /project
RUN mvn clean package -DskipTests

FROM adoptopenjdk/openjdk11:jre-11.0.9.1_1-alpine
RUN mkdir /app
COPY --from=build /project/target/admin-0.0.1-SNAPSHOT.jar /app/admin-0.0.1-SNAPSHOT.jar
WORKDIR /app
EXPOSE 8080
CMD "java" "-jar" "admin-0.0.1-SNAPSHOT.jar"
