# Stage 1: Build the application
FROM openjdk:17-jdk-alpine AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the entire application to the container
COPY . .

# Install Maven
RUN apk update && apk add --no-cache maven

# Build the application
RUN mvn package -DskipTests

# Stage 2: Package the application
FROM openjdk:17-jdk-alpine

# Copy the JAR file from the build stage
COPY --from=build /app/target/agro_back-0.0.1-SNAPSHOT.jar /app/agro_back.jar

# Define the entry point for the container
ENTRYPOINT ["java", "-jar", "/app/agro_back.jar"]
