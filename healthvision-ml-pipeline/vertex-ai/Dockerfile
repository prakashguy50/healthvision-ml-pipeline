# ==================== BUILD STAGE ====================
FROM maven:3.8.6-eclipse-temurin-11 AS builder

WORKDIR /build
COPY pom.xml ./ 
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# ==================== RUNTIME STAGE ====================
FROM eclipse-temurin:11-jre-jammy

WORKDIR /app

# Vertex AI required paths
ENV AIP_MODEL_DIR=/app/model
ENV AIP_DATA_DIR=/app/data
RUN mkdir -p ${AIP_MODEL_DIR} ${AIP_DATA_DIR}

# Copy the JAR and dataset from resources
COPY --from=builder /build/target/ml-pipeline-*.jar ./app.jar
COPY --from=builder /build/src/main/resources/datasets/iris.csv ${AIP_DATA_DIR}/iris.csv

# Expose port 8080 for Cloud Run
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
