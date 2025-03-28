# ==================== BUILD STAGE ====================
FROM maven:3.8.6-jdk-11 AS builder

WORKDIR /build
COPY pom.xml .
# Download dependencies first (cached unless pom.xml changes)
RUN mvn dependency:go-offline

COPY src ./src
COPY datasets ./datasets
# Build the application
RUN mvn clean package -DskipTests

# ==================== RUNTIME STAGE ====================
FROM openjdk:11-jre-slim

WORKDIR /app
# Copy built JAR from builder stage
COPY --from=builder /build/target/ml-pipeline-1.0-SNAPSHOT.jar ./ml-pipeline.jar
COPY --from=builder /build/datasets/iris.csv ./datasets/

# Lightweight GCloud CLI installation
RUN apt-get update && \
    apt-get install -y curl python3 && \
    curl -sSL https://sdk.cloud.google.com > /tmp/install.sh && \
    bash /tmp/install.sh --install-dir=/opt --disable-prompts && \
    ln -s /opt/google-cloud-sdk/bin/gcloud /usr/bin/gcloud && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/install.sh

ENTRYPOINT ["java", "-jar", "ml-pipeline.jar"]