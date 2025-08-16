FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .

# Cache dependencies
RUN mvn dependency:go-offline -B

COPY src ./src

# Package into jar
RUN mvn clean package

FROM openjdk:21-jdk AS runner

WORKDIR /app

COPY --from=builder /app/target/billing-service-0.0.1-SNAPSHOT.jar ./app.jar

# REST
EXPOSE 4001
# GRPC
EXPOSE 9001

ENTRYPOINT ["java", "-jar", "app.jar"]