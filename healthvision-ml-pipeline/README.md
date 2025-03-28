# HealthVision ML Pipeline

## Overview
Java-based ML pipeline for GCP Vertex AI with:
- Automated training workflows
- Model deployment
- GCS integration

## Setup
1. Set GitHub secrets:
   - `GCP_PROJECT`
   - `GCS_BUCKET` 
   - `GCP_SA_KEY`

2. Configure `config.properties`

## Workflows
- **Training**: Triggered on code changes
- **Deployment**: Auto-deploys successful models

## Local Development
```bash
mvn clean package
java -Dgcs.bucket=your-bucket -jar target/ml-pipeline-1.0.jar


### **Implementation Notes**
1. The workflow files include:
   - Automatic training on code changes
   - Conditional deployment after successful training
   - GCP authentication via GitHub secrets

2. The Java implementation:
   - Uses Weka for ML (no Python required)
   - Includes proper error handling
   - Follows clean code practices

3. The Dockerfile:
   - Creates a minimal image with JDK + GCloud SDK
   - Supports both training and prediction

To use this:
1. Clone the repo
2. Set up secrets in GitHub
3. Push to trigger the pipeline

Would you like me to explain any specific part in more detail?