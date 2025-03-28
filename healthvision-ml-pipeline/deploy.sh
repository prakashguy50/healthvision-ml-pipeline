#!/bin/bash
set -euo pipefail

# Configuration
GCP_PROJECT=$(gcloud config get-value project)
GCP_REGION="us-central1"
TIMESTAMP=$(date +%Y%m%d-%H%M%S)

# Get latest training job ID
JOB_ID=$(gcloud ai custom-jobs list \
  --region=$GCP_REGION \
  --format="value(name)" \
  --sort-by="~createTime" \
  --limit=1)

if [ -z "$JOB_ID" ]; then
  echo "❌ No training jobs found in region $GCP_REGION"
  exit 1
fi

# Execute deployment
echo "🚀 Starting deployment for job $JOB_ID..."
./scripts/deploy-model.sh "$JOB_ID" "$GCP_PROJECT" "$GCP_REGION" "$TIMESTAMP"

# Verify deployment
ENDPOINT_ID=$(gcloud ai endpoints list \
  --region=$GCP_REGION \
  --filter="displayName=iris-endpoint" \
  --format="value(name)" \
  --limit=1)

if [ -n "$ENDPOINT_ID" ]; then
  ./scripts/verify-deployment.sh "$ENDPOINT_ID" "$GCP_PROJECT"
else
  echo "❌ Failed to find deployed endpoint"
  exit 1
fi