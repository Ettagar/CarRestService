FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /car-rest-service
COPY pom.xml ./
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests
FROM openjdk:17-jdk-slim
WORKDIR /car-rest-service
COPY --from=build /car-rest-service/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]