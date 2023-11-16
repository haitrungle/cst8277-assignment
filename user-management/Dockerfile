# Use a base image with Java and Maven pre-installed
FROM maven:3.9.5-eclipse-temurin-17-alpine AS build

# Set the working directory in the container
WORKDIR /app

# Copy the project's pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code to the container
COPY src ./src

# Build the application
RUN mvn package -DskipTests

# Create a new image with only the compiled application
FROM eclipse-temurin:17-jre-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the executable JAR from the build image
COPY --from=build /app/target/*.jar usm.jar

# Set the command to run the Spring Boot application
CMD ["java", "-jar", "usm.jar"]