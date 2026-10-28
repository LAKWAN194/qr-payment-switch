# Roadmap

Eight weeks, planned from **Monday 16 November 2026** (after exams) to
**Sunday 10 January 2027**. Each week has a goal, tasks, the decisions due before
coding starts, and a "done when" check. Tick tasks off in this file as you go, so
the commit history shows the progress.

The ADR numbers refer to the backlog in [adr/README.md](adr/README.md).

## Week 0: skeleton (done)

- [x] Maven multi-module build (`switch-service`, `mock-issuer`) on Spring Boot 4.1
- [x] Testcontainers integration test against PostgreSQL 17
- [x] ArchUnit rule that keeps the QR kernel free of framework code
- [x] FastAPI `fraud-scorer` with health check, tests and ruff
- [x] React + TypeScript `merchant-dashboard` with Vitest, oxlint and a working `/api` proxy
- [x] `compose.yaml`: PostgreSQL, Redpanda (Kafka API), Redpanda Console, Redis, and every service under the `apps` profile
- [x] CI for all three languages
- [x] Spec notes, design templates, ADR backlog, test plan, security checklist

## Week 1 (16–22 Nov): the ledger and the data model

**Goal:** a schema you can defend, created by Flyway, with the money rules
enforced as far down as possible.

Decisions due: ADR-001 (service boundaries), ADR-002 (money representation),
ADR-003 (ledger design).

- [ ] Read [domain/payments-primer.md](domain/payments-primer.md), sections 1–2
- [ ] Write ADR-001, ADR-002 and ADR-003
- [ ] Draw the ER diagram in [design/data-model.md](design/data-model.md): accounts, journal entries, postings, merchants, payments
- [ ] Decide which invariants the database enforces (constraints, triggers) and which Java enforces; write the split down
- [ ] Write the first Flyway migrations (`switch-service/src/main/resources/db/migration`)
- [ ] Repository tests with Testcontainers: a balanced posting is accepted, an unbalanced one is rejected
- [ ] Start the decision log in [interview-prep.md](interview-prep.md)

**Done when:** migrations apply in CI, the ER diagram is in the repo, and a test
proves an unbalanced journal entry cannot be saved.

## Week 2 (23–29 Nov): the kernel and merchant onboarding

**Goal:** your own EMV QR codec, and merchants who can sign up and get a code.

Decisions due: ADR-004 (merchant identity and QR types).

- [ ] Implement `Crc16` by hand and enable `Crc16Test`
- [ ] Design and implement the TLV codec: build a payload, parse one, validate it ([design/kernel.md](design/kernel.md))
- [ ] Test it against `src/test/resources/emv-qr/vectors.tsv`
- [ ] Write property-based tests with jqwik (round trip, corruption detected, lengths respected)
- [ ] Design the merchant onboarding endpoints in [design/openapi.yaml](design/openapi.yaml), then implement them
- [ ] Return the merchant's static QR payload, and render it as an image for the dashboard later

**Done when:** all vectors pass, the property tests pass with jqwik's default
number of tries, and you can write the CRC loop on paper from memory.

## Week 3 (30 Nov – 6 Dec): payments, idempotency and the state machine

**Goal:** a payment API that is safe to retry and safe under concurrent requests.

Decisions due: ADR-005 (idempotency), ADR-006 (state machine implementation),
ADR-007 (concurrency control).

- [ ] Draw the state machine in [design/state-machine.md](design/state-machine.md), including every allowed transition and what triggers it
- [ ] Design the payment endpoints and the idempotency-key behaviour in `openapi.yaml`
- [ ] Implement them, posting to the ledger inside the same transaction
- [ ] Integration test: two concurrent requests with the same idempotency key create one payment
- [ ] Integration test: an illegal transition (for example settling a reversed payment) is rejected
- [ ] Optimistic locking (`@Version`) or row locks, as ADR-007 decided, with a test that forces the conflict

**Done when:** the concurrency tests pass reliably (run them 20 times in a row).

## Week 4 (7–13 Dec): routing to issuers over Kafka

**Goal:** authorisations travel to two mock banks and back, and nothing is lost
when Kafka or a bank misbehaves.

Decisions due: ADR-008 (event publishing), ADR-009 (topics, keys and message
contracts), ADR-010 (issuer timeouts and reversals).

- [ ] Add `spring-boot-starter-kafka` to both modules and `testcontainers-redpanda` (or `testcontainers-kafka`) for tests
- [ ] Write the message contracts in [design/messages.md](design/messages.md)
- [ ] Implement the outbox table and its publisher
- [ ] Make the mock issuer consume requests and reply; give it switches for slow replies, declines and silence
- [ ] Time out unanswered authorisations and reverse them
- [ ] Failure drill: stop Redpanda mid-payment, start it again, and show nothing was lost or doubled. Write the postmortem.

**Done when:** an integration test runs a payment end to end through Redpanda,
and the drill's postmortem is in `docs/postmortems/`.

## Week 5 (14–20 Dec): fraud scoring on the hot path

**Goal:** a model that is useful, fast, and safe when it fails.

Decisions due: ADR-011 (scorer failure policy), ADR-012 (threshold and its cost).

- [ ] Build the synthetic transaction generator in `tools/txn-generator` (velocity, amount spikes, new devices)
- [ ] Train XGBoost and a baseline; split by time; pick the threshold from the precision-recall curve (`fraud-scorer/training`)
- [ ] Design the scoring endpoint (request, response, latency budget) and implement it
- [ ] Call it from the switch through a circuit breaker (`resilience4j-spring-boot4`) with a timeout
- [ ] Integration test: the scorer is down, and the switch follows ADR-011
- [ ] Failure drill: kill the scorer under load. Write the postmortem.

**Done when:** the model card is written, and the switch's behaviour with the
scorer down is tested and matches the ADR.

## Week 6 (21–27 Dec): webhooks and reconciliation

**Goal:** merchants hear about every change exactly as often as they need to, and
the books match the banks.

Decisions due: ADR-013 (webhook signing), ADR-014 (retries and dead letters),
ADR-015 (reconciliation).

- [ ] Webhook contract and signature scheme in [design/webhooks.md](design/webhooks.md)
- [ ] Delivery with exponential backoff and jitter, and a dead-letter queue
- [ ] A small receiver in the test suite that verifies signatures and rejects replays
- [ ] Nightly settlement job and a reconciliation report against the mock issuers' records
- [ ] Failure drill: deliver the same webhook twice, and replay an old one. Write the postmortem.

**Done when:** reconciliation of clean data reports zero mismatches, and seeded
mismatches are all caught.

## Week 7 (28 Dec – 3 Jan): dashboard, login, full stack, load test

**Goal:** something you can demo in five minutes, with numbers behind it.

Decisions due: ADR-016 (authentication and authorisation).

- [ ] Merchant dashboard screens: payments, settlements, webhook deliveries
- [ ] Asgardeo OIDC login for the dashboard; the switch validates tokens (`spring-boot-starter-security-oauth2-resource-server`)
- [ ] `docker compose --profile apps up` runs the whole demo
- [ ] k6 load test (`tools/load-test`); record p95 latency and throughput in the README
- [ ] Fill in the "fast enough" numbers in [brief.md](brief.md)

**Done when:** a fresh clone runs the demo from the README alone.

## Week 8 (4–10 Jan): security pass and write-up

**Goal:** the project survives a hostile reviewer and explains itself.

- [ ] Work through [security.md](security.md): IDOR on merchant endpoints, injection, webhook replay, secrets, rate limits
- [ ] Fix what you find, with a test for each fix
- [ ] README: architecture diagram, decisions, numbers, demo GIF or video
- [ ] Blog post (for example "Securing a LankaQR-style payment API with Asgardeo"), which also counts towards WSO2 points
- [ ] Rehearse the five-minute walkthrough and the whiteboard sketch in [interview-prep.md](interview-prep.md)

**Done when:** you can give the walkthrough without notes, and every "decision you
must defend" has an answer in the decision log.
