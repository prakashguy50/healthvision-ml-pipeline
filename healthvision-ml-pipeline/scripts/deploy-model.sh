#!/bin/bash
set -euo pipefail

# Input parameters
GCP_PROJECT=${1:-$(gcloud config get-value project)}
GCP_REGION=${2:-us-central1}
MODEL_NAME="iris-model-$(date +%Y%m%d-%H%M%S)"
ENDPOINT_NAME="iris-endpoint"
SERVICE_ACCOUNT="vertex-ai-service-account@${GCP_PROJECT}.iam.gserviceaccount.com"

# Validate inputs
[ -z "$GCP_PROJECT" ] && { echo "❌ GCP project not specified"; exit 1; }

# Get latest successful training job
echo "🔍 Finding latest training job..."
JOB_ID=$(gcloud ai custom-jobs list \
  --region=$GCP_REGION \
  --filter="state=JOB_STATE_SUCCEEDED" \
  --format="value(name)" \
  --sort-by="~createTime" \
  --limit=1)

[ -z "$JOB_ID" ] && { echo "❌ No successful training jobs found"; exit 1; }
JOB_ID_BASE=$(basename $JOB_ID)
echo "✔ Found job: $JOB_ID"

# Model artifact location - multiple fallback options
echo "🔄 Locating model artifacts..."
POSSIBLE_PATHS=(
  "gs://${GCP_PROJECT}-aiplatform-outputs/${JOB_ID_BASE}/model"
  "gs://${GCP_PROJECT}-model-outputs/${JOB_ID_BASE}/model"
  "gs://healthvision-ml-dev-model-outputs/${JOB_ID_BASE}/model"
  "gs://healthvision-ml-dev-aiplatform-outputs/${JOB_ID_BASE}/model"
)

MODEL_DIR=""
for path in "${POSSIBLE_PATHS[@]}"; do
  if gsutil ls "${path}" >/dev/null 2>&1; then
    MODEL_DIR="${path}"
    break
  fi
done

[ -z "${MODEL_DIR}" ] && {
  echo "❌ Could not locate model artifacts. Checked:"
  printf '  - %s\n' "${POSSIBLE_PATHS[@]}"
  exit 1
}
echo "✔ Model artifacts found at: $MODEL_DIR"

# Register model with versioning
echo "📦 Registering model version: $MODEL_NAME..."
MODEL_ID=$(gcloud ai models upload \
  --region=$GCP_REGION \
  --project=$GCP_PROJECT \
  --display-name="$MODEL_NAME" \
  --container-image-uri="gcr.io/$GCP_PROJECT/ml-pipeline:prod-final" \
  --artifact-uri="$MODEL_DIR" \
  --format="value(name)" || { echo "❌ Model registration failed"; exit 1; })

# Endpoint management
echo "🔌 Configuring endpoint: $ENDPOINT_NAME..."
ENDPOINT_ID=$(gcloud ai endpoints list \
  --region=$GCP_REGION \
  --project=$GCP_PROJECT \
  --filter="displayName=$ENDPOINT_NAME" \
  --format="value(name)" | head -1)

if [ -z "$ENDPOINT_ID" ]; then
  ENDPOINT_ID=$(gcloud ai endpoints create \
    --project=$GCP_PROJECT \
    --region=$GCP_REGION \
    --display-name="$ENDPOINT_NAME" \
    --format="value(name)" || { echo "❌ Endpoint creation failed"; exit 1; })
fi

# Deployment with traffic splitting
echo "🚀 Deploying model to endpoint..."
gcloud ai endpoints deploy-model $ENDPOINT_ID \
  --project=$GCP_PROJECT \
  --region=$GCP_REGION \
  --model=$MODEL_ID \
  --display-name="$MODEL_NAME" \
  --machine-type="n1-standard-4" \
  --min-replica-count=1 \
  --max-replica-count=2 \
  --traffic-split="0=100" \
  --service-account="$SERVICE_ACCOUNT" \
  --disable-container-logging

echo "✅ Deployment Successful!"
echo "-----------------------------"
echo "Project:    $GCP_PROJECT"
echo "Endpoint:   $ENDPOINT_ID"
echo "Model:      $MODEL_ID"
echo "Model Dir:  $MODEL_DIR"
echo "-----------------------------"

# Verification
echo "🔍 Starting verification..."
./scripts/verify-deployment.sh "$ENDPOINT_ID" "$GCP_PROJECT" || exit 1