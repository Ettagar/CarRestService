# Use the official Maven image with OpenJDK 17
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /car-rest-service

# Copy only the pom.xml and resolve dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application (skip tests if needed)
RUN mvn clean package -DskipTests

# Use a lightweight image for running the application
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /car-rest-service

# Copy the built JAR from the previous stage
COPY --from=build /car-rest-service/target/*.jar app.jar

# Expose the default port for Spring Boot
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.jar"]