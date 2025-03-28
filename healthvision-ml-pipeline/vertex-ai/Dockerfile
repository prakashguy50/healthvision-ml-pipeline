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

# Vertex AI required paths (MUST include these)
ENV AIP_MODEL_DIR=/app/model
ENV AIP_DATA_DIR=/app/data
RUN mkdir -p ${AIP_MODEL_DIR} ${AIP_DATA_DIR}

# Copy artifacts to Vertex AI standard paths
COPY --from=builder /build/target/ml-pipeline-*.jar ./app.jar
COPY --from=builder /build/datasets/iris.csv ${AIP_DATA_DIR}/iris.csv

# Simplified runtime - remove unnecessary dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

# Vertex AI-optimized entrypoint
ENTRYPOINT ["sh", "-c", "java -Djava.util.logging.SimpleFormatter.format='[%1$tF %1$tT] [%4$-7s] %5$s %n' -cp app.jar com.healthvision.ml.Trainer 2>&1 | tee ${AIP_MODEL_DIR}/output.log"]