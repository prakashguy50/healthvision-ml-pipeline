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

# Ensure proper dataset path:
COPY --from=builder /build/datasets/iris.csv /app/iris.csv

# Minimal GCloud CLI (static install)
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    curl -sSL https://dl.google.com/dl/cloudsdk/channels/rapid/downloads/google-cloud-cli-454.0.0-linux-x86.tar.gz | \
    tar -xz -C /usr/local && \
    apt-get remove -y curl && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["java", "-cp", "app.jar", "com.healthvision.ml.Trainer"]