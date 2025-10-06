FROM openjdk:21-jdk-slim AS builder
LABEL authors="sergejspodarev"
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:21-jre-slim
LABEL authors="sergejspodarev"
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]