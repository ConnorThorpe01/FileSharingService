#!/bin/bash

# Print message indicating the start of the script
echo "Starting build script..."

# Clean the project
echo "Cleaning the project..."
./gradlew clean

# Check if clean was successful
if [ $? -ne 0 ]; then
  echo "Failed to clean the project. Exiting..."
  exit 1
fi

# Build the project
echo "Building the project..."
./gradlew build

# Check if build was successful
if [ $? -ne 0 ]; then
  echo "Failed to build the project. Exiting..."
  exit 1
fi

# Print message indicating the end of the script
echo "Build script completed successfully."
