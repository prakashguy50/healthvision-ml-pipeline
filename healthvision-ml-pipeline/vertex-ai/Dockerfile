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

# Minimal GCloud CLI installation (with Python3)
RUN apt-get update && \
    apt-get install -y curl python3-minimal && \
    curl -sSL https://sdk.cloud.google.com > /tmp/install.sh && \
    bash /tmp/install.sh --disable-prompts --install-dir=/usr/local && \
    rm -rf /var/lib/apt/lists/* /tmp/install.sh

ENTRYPOINT ["java", "-jar", "app.jar"]