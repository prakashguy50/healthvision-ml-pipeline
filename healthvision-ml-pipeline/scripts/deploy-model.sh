#!/bin/bash
set -euo pipefail

[Your complete fixed deploy-model.sh content from earlier]
# Added custom model path handling
POSSIBLE_PATHS=(
  "gs://${GCP_PROJECT}-aiplatform-outputs/${JOB_ID_BASE}/model"
  "gs://${GCP_PROJECT}-model-outputs/${JOB_ID_BASE}/model"
  "gs://healthvision-ml-dev-model-outputs/${JOB_ID_BASE}/model" 
  "gs://healthvision-ml-dev-aiplatform-outputs/${JOB_ID_BASE}/model"
)

for path in "${POSSIBLE_PATHS[@]}"; do
  if gsutil ls "${path}" >/dev/null 2>&1; then
    MODEL_DIR="${path}"
    break
  fi
done
