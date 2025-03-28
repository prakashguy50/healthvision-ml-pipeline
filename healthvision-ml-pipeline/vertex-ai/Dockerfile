# ==================== BUILD STAGE ====================
FROM maven:3.8.6-eclipse-temurin-11 AS builder

WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
COPY datasets ./datasets
RUN mvn clean package -DskipTests -B

# ==================== RUNTIME STAGE ====================
FROM eclipse-temurin:11-jre-jammy

WORKDIR /app
COPY --from=builder /build/target/ml-pipeline-*.jar ./app.jar
COPY --from=builder /build/datasets/iris.csv ./iris.csv

# Install minimal dependencies and clean up
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    python3 \
    python3-pip && \
    pip3 install --no-cache-dir google-cloud-aiplatform && \
    rm -rf /var/lib/apt/lists/*

# Set environment variables
ENV AIP_MODEL_DIR=/app/model
ENV AIP_DATA_DIR=/app/data

# Create required directories
RUN mkdir -p ${AIP_MODEL_DIR} ${AIP_DATA_DIR}

# Health check (optional)
HEALTHCHECK --interval=30s --timeout=5s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/health || exit 1

# Improved entrypoint with error handling
ENTRYPOINT ["sh", "-c", "java -cp app.jar com.healthvision.ml.Trainer || { echo 'Application failed'; exit 1; }"]