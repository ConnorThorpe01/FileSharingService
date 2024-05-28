#!/bin/bash

# Define your service name and volume name
SERVICE_NAME="file-service"
VOLUME_NAME="filesharing_grpc_data
"

# Stop and remove the container
echo "Stopping and removing the Docker container..."
docker-compose down

# Remove the volume
echo "Removing the Docker volume..."
docker volume rm "${SERVICE_NAME}_${VOLUME_NAME}"

# Build the Docker image
echo "Building the Docker image..."
docker-compose build

# Start the container
echo "Starting the Docker container..."
docker-compose up -d

echo "Docker container and volume reset completed."
