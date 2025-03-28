# File: Makefile
SHELL := /bin/bash
GCP_PROJECT := $(shell gcloud config get-value project)
GCP_REGION := us-central1

deploy: validate
	@echo "🚀 Starting deployment to $(GCP_PROJECT)..."
	@./deploy.sh $(GCP_PROJECT) $(GCP_REGION)

validate:
	@echo "🔍 Validating environment..."
	@which gcloud || (echo "❌ Error: gcloud not found"; exit 1)
	@test -f deploy.sh || (echo "❌ Error: Missing deploy.sh"; exit 1)
	@test -f scripts/deploy-model.sh || (echo "❌ Error: Missing deploy-model.sh"; exit 1)
	@test -n "$(GCP_PROJECT)" || (echo "❌ Error: GCP project not set"; exit 1)
	@echo "✅ All checks passed"

.PHONY: deploy validate