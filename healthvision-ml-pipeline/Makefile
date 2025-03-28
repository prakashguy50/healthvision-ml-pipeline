# File: Makefile
SHELL := /bin/bash
GCP_PROJECT := $(shell gcloud config get-value project 2>/dev/null)
GCP_REGION := us-central1
ENDPOINT_NAME := iris-endpoint
MODEL_NAME := iris-model-$(shell date +%Y%m%d)

deploy: validate
	@echo "🚀 Starting deployment..."
	@./scripts/deploy.sh $(GCP_PROJECT) $(GCP_REGION) $(ENDPOINT_NAME) $(MODEL_NAME)

validate:
	@echo "🔍 Validating..."
	@which gcloud || (echo "❌ gcloud missing"; exit 1)
	@test -f scripts/deploy.sh || (echo "❌ deploy.sh missing"; exit 1)
	@test -n "$(GCP_PROJECT)" || (echo "❌ GCP project not set"; exit 1)
	@echo "✅ All checks passed"

status:
	@echo "📊 Current endpoints:"
	@gcloud ai endpoints list --region=$(GCP_REGION) --format="table(name,displayName,createTime)"

.PHONY: deploy validate status