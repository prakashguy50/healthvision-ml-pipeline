# ==================== BUILD STAGE ====================
FROM openjdk:11-jdk-slim AS builder

WORKDIR /build
COPY . .
RUN mvn clean package

# ==================== RUNTIME STAGE ====================
FROM openjdk:11-jre-slim

# Set working directory
WORKDIR /app

# Copy built artifacts from builder stage
COPY --from=builder /build/target/ml-pipeline-1.0-SNAPSHOT.jar ./ml-pipeline.jar
COPY --from=builder /build/datasets/iris.csv ./datasets/

# Install minimal GCloud components (only gcloud CLI)
RUN apt-get update && \
    apt-get install -y curl python3 && \
    curl -sSL https://sdk.cloud.google.com > /tmp/install.sh && \
    bash /tmp/install.sh --install-dir=/opt --disable-prompts && \
    ln -s /opt/google-cloud-sdk/bin/gcloud /usr/bin/gcloud && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/install.sh

# Environment configuration
ENV PATH="/opt/google-cloud-sdk/bin:${PATH}"

# Health check (optional)
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/health || exit 1

# Runtime configuration
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/ml-pipeline.jar"]