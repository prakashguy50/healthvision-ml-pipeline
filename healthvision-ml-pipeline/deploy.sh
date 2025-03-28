#!/bin/bash
set -euo pipefail

GCP_PROJECT=$1
GCP_REGION=$2

echo "▶️ Starting deployment pipeline..."
echo "Project: $GCP_PROJECT"
echo "Region: $GCP_REGION"

# Execute deployment script
./scripts/deploy-model.sh "$GCP_PROJECT" "$GCP_REGION"

echo "✅ Deployment completed successfully"