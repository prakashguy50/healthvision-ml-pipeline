FROM openjdk:11-jdk-slim

WORKDIR /app

# Copy built JAR and dependencies
COPY target/ml-pipeline-1.0.jar .
COPY datasets/iris.csv ./datasets/

# Install GCloud SDK for Vertex AI integration
RUN apt-get update && \
    apt-get install -y curl python3 && \
    curl -sSL https://sdk.cloud.google.com | bash && \
    ln -s /root/google-cloud-sdk/bin/gcloud /usr/bin/gcloud

ENV PATH="/root/google-cloud-sdk/bin:${PATH}"

ENTRYPOINT ["java", "-jar", "ml-pipeline-1.0.jar"]