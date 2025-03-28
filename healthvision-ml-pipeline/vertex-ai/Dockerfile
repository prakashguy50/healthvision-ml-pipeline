# ==================== BUILD STAGE ====================
FROM maven:3.8.6-jdk-11 AS builder

WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# ==================== RUNTIME STAGE ====================
FROM openjdk:11-jre-slim

WORKDIR /app
COPY --from=builder /build/target/ml-pipeline-*.jar ./app.jar
COPY --from=builder /build/datasets/iris.csv .

# Minimal GCloud CLI (no Python)
RUN apt-get update && \
    apt-get install -y curl && \
    curl -sSL https://sdk.cloud.google.com | bash && \
    rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["java", "-jar", "app.jar"]