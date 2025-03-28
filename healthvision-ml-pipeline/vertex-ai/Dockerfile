# ==================== BUILD STAGE ====================
FROM maven:3.8.6-jdk-11 AS builder

WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
COPY datasets ./datasets
RUN mvn clean package -DskipTests -B

# ==================== RUNTIME STAGE ====================
FROM openjdk:11-jre-slim

WORKDIR /app
COPY --from=builder /build/target/ml-pipeline-*.jar ./app.jar
COPY --from=builder /build/datasets/iris.csv .

# Minimal GCloud CLI (static install - no Python needed)
RUN apt-get update && \
    apt-get install -y curl && \
    curl -O https://dl.google.com/dl/cloudsdk/channels/rapid/downloads/google-cloud-cli-454.0.0-linux-x86_64.tar.gz && \
    tar -xf google-cloud-cli-*.tar.gz -C /usr/local && \
    /usr/local/google-cloud-sdk/install.sh --usage-reporting=false --command-completion=false --path-update=false && \
    rm google-cloud-cli-*.tar.gz && \
    apt-get remove -y curl && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["java", "-jar", "app.jar"]