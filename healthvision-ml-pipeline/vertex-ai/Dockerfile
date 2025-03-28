FROM openjdk:11-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY target/ml-pipeline-1.0-SNAPSHOT.jar /app/ml-pipeline.jar

# Optional: Copy any necessary datasets or additional files
COPY datasets/iris.csv ./datasets/

# Install GCloud SDK for Vertex AI integration
RUN apt-get update && \
    apt-get install -y curl python3 && \
    curl -sSL https://sdk.cloud.google.com | bash && \
    ln -s /root/google-cloud-sdk/bin/gcloud /usr/bin/gcloud

# Add GCloud SDK to the PATH
ENV PATH="/root/google-cloud-sdk/bin:${PATH}"

# Set the entry point for the container
ENTRYPOINT ["java", "-jar", "/app/ml-pipeline.jar"]
