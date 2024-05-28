# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the build.gradle, settings.gradle, and gradlew files
COPY build.gradle settings.gradle gradlew /app/
COPY gradle /app/gradle

# Copy the source code
COPY src /app/src

# Copy the build script
COPY build.sh /app/

ENV DATA_DIR=/data
# Run the build script to build the project
RUN ./build.sh

# Expose the port the application runs on
EXPOSE 8080

# Run the gRPC server
CMD ["java", "-jar", "build/libs/file-sharing-service-all.jar", "-s"]
