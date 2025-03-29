# ==================== BUILD STAGE ====================
FROM maven:3.8.6-eclipse-temurin-11 AS builder

WORKDIR /build
COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src ./src
COPY datasets ./datasets
RUN mvn clean package -DskipTests -B

# ==================== RUNTIME STAGE ====================
FROM eclipse-temurin:11-jre-jammy

WORKDIR /app

# Vertex AI required paths (MUST include these)
ENV AIP_MODEL_DIR=/app/model
ENV AIP_DATA_DIR=/app/data
RUN mkdir -p ${AIP_MODEL_DIR} ${AIP_DATA_DIR}

# Copy artifacts to Vertex AI standard paths
COPY --from=builder /build/target/ml-pipeline-*.jar ./app.jar
COPY --from=builder /build/datasets/iris.csv ${AIP_DATA_DIR}/iris.csv

# Expose port 8080 for Cloud Run
EXPOSE 8080

# Vertex AI-optimized entrypoint for Cloud Run
ENTRYPOINT ["java", "-jar", "app.jar"]
