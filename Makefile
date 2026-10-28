.DEFAULT_GOAL := help
.PHONY: help setup infra-up infra-down up down clean-data run-switch run-issuer-a run-issuer-b \
        run-scorer run-dashboard test test-java test-python test-web load-smoke

help: ## List the targets
	@grep -E '^[a-zA-Z_-]+:.*## ' $(MAKEFILE_LIST) | awk -F':.*## ' '{printf "  %-15s %s\n", $$1, $$2}'

setup: ## Install the Python and Node dependencies
	cd fraud-scorer && python3 -m venv .venv && .venv/bin/pip install -r requirements-dev.txt
	cd merchant-dashboard && npm install

infra-up: ## Start PostgreSQL, Redpanda, Redpanda Console and Redis
	docker compose up -d --wait

infra-down: ## Stop the infrastructure (keeps data)
	docker compose down

up: ## Build and start the whole system in Docker
	docker compose --profile apps up -d --build --wait

down: ## Stop everything (keeps data)
	docker compose --profile apps down

clean-data: ## Stop everything and delete the database volume
	docker compose --profile apps down -v

run-switch: ## Run switch-service locally on :8180 (needs infra-up)
	./mvnw -pl switch-service spring-boot:run

run-issuer-a: ## Run mock issuer BANK_A locally on :8181
	ISSUER_ID=BANK_A ISSUER_PORT=8181 ./mvnw -pl mock-issuer spring-boot:run

run-issuer-b: ## Run mock issuer BANK_B locally on :8182
	ISSUER_ID=BANK_B ISSUER_PORT=8182 ./mvnw -pl mock-issuer spring-boot:run

run-scorer: ## Run fraud-scorer locally on :8183
	cd fraud-scorer && .venv/bin/uvicorn app.main:app --reload --port 8183

run-dashboard: ## Run the dashboard dev server on :8185
	cd merchant-dashboard && npm run dev

test: test-java test-python test-web ## Run every test in every language

test-java: ## Java unit and integration tests (needs Docker)
	./mvnw -B verify

test-python: ## Fraud scorer lint and tests
	cd fraud-scorer && .venv/bin/ruff check . && .venv/bin/ruff format --check . && .venv/bin/pytest

test-web: ## Dashboard lint, tests and build
	cd merchant-dashboard && npm run lint && npm test && npm run build

load-smoke: ## 30-second k6 smoke test against the running switch
	docker run --rm -i --network host -e SWITCH_URL=http://localhost:8180 grafana/k6:1.3.0 run - < tools/load-test/smoke.js
