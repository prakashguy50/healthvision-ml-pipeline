# ==================== BUILD STAGE ====================
FROM maven:3.8.6-jdk-11 AS builder

WORKDIR /build
# First copy only POM to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Then copy everything else
COPY src ./src
COPY datasets ./datasets
RUN mvn clean package -DskipTests -B

# ==================== RUNTIME STAGE ====================
FROM openjdk:11-jre-slim

WORKDIR /app
# Copy built JAR (using wildcard for version flexibility)
COPY --from=builder /build/target/ml-pipeline-*.jar ./app.jar
# Copy dataset (verify path exists in builder)
COPY --from=builder /build/datasets/iris.csv ./iris.csv

# Minimal GCloud CLI
RUN apt-get update && \
    apt-get install -y curl && \
    curl -sSL https://sdk.cloud.google.com | bash && \
    rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["java", "-jar", "app.jar"]