# Dockerfile for Restful API application using Spring Boot and Maven
# This Dockerfile uses a multi-stage build to create a lightweight image for the application.

# ---------- Stage 1: Build the application ----------
FROM maven:3.8.8-eclipse-temurin-21-alpine AS builder

# Set the working directory in the builder stage
WORKDIR /app

# Copy pom.xml and download dependencies
COPY ./pom.xml .

# Copy source code and build
COPY ./src ./src
RUN mvn clean package -DskipTests

# Clean up local Maven repo (optional)
# RUN rm -rf ~/.m2/repository


# ---------- Stage 2: Create runtime image ----------
FROM eclipse-temurin:21-alpine

# Set the working directory in the runtime stage
WORKDIR /app

# Define the build argument for the environment file
ARG SERVER_PORT

# Copy the private and public key files into the image
COPY ./src/main/resources/privateKey.pem /app/src/main/resources/privateKey.pem
COPY ./src/main/resources/publicKey.pem /app/src/main/resources/publicKey.pem

# Copy only the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE ${SERVER_PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]