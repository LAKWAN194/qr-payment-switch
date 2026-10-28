# Test strategy

## Layers

| Layer | What it proves | Tools (already wired up) | Naming | Runs in |
| --- | --- | --- | --- | --- |
| Unit | One class's logic, with no I/O | JUnit 5, AssertJ, Mockito | `*Test.java` | `./mvnw test` |
| Property-based | A rule holds for thousands of generated inputs | jqwik | `*Test.java` or `*Properties.java` | `./mvnw test` |
| Architecture | Package rules, such as the kernel staying framework-free | ArchUnit | `*Test.java` | `./mvnw test` |
| Integration | The service against real PostgreSQL (and later Redpanda and Redis) | Spring Boot test, Testcontainers | `*IT.java` | `./mvnw verify` |
| Contract | Each side of an API or message agrees on its shape | Your choice (week 4) | | |
| End to end | A payment through the whole running system | `docker compose`, a test script | | Week 7 |
| Load | p95 latency and throughput | k6 (`tools/load-test`) | | Week 7 |
| Python | The scorer's API and training code | pytest, ruff | `test_*.py` | `pytest` |
| Dashboard | Components and API calls | Vitest, Testing Library | `*.test.tsx` | `npm test` |

`./mvnw verify` runs unit tests first (Surefire), then integration tests (Failsafe),
and writes coverage reports to `*/target/site/jacoco*/`. Integration tests need
Docker running.

## Invariants

These must stay true no matter what fails. Write each test **before** the code that
could break it, and keep them as the backbone of the suite. The left column is the
rule; how you test it is your design.

| Invariant | Week | Test |
| --- | --- | --- |
| Money is never created or destroyed: every journal entry's debits equal its credits | 1 | |
| Ledger postings are never updated or deleted | 1 | |
| A balance equals the sum of that account's postings | 1 | |
| A valid QR payload round-trips through build and parse unchanged | 2 | |
| Any single-character change to a valid QR payload is detected | 2 | |
| Two requests with the same idempotency key cause the effect once | 3 | |
| The same idempotency key with a different request is rejected | 3 | |
| Only legal state transitions happen, and terminal states never change | 3 | |
| Every state change that needs a message has exactly one outbox row, written in the same transaction | 4 | |
| A duplicate message from Kafka has no effect | 4 | |
| An authorisation with no reply is reversed, never left pending forever | 4 | |
| With the scorer down, the switch follows the ADR-011 policy | 5 | |
| A webhook with a bad signature or an old timestamp is rejected by the receiver | 6 | |
| A webhook delivered twice has no extra effect on the receiver | 6 | |
| Reconciliation of matching records reports zero mismatches, and catches each seeded mismatch | 6 | |
| One merchant can never read or change another merchant's data | 7–8 | |

## Failure drills

Break the system on purpose, watch what happens, fix it, and write a postmortem in
[postmortems/](postmortems/) using the template. Each one becomes an answer to "tell
me about a hard problem you solved".

| Drill | Week | How |
| --- | --- | --- |
| Kafka goes away mid-payment | 4 | `docker compose stop redpanda` during a load run, then start it again |
| The same payment request arrives twice at once | 3 | Two concurrent requests with one idempotency key |
| An issuer stops answering | 4 | Mock issuer "silent" switch |
| The fraud scorer dies under load | 5 | `docker compose stop fraud-scorer` during a k6 run |
| A webhook is delivered twice, and an old one is replayed | 6 | Resend a captured request |
| The switch crashes between the database commit and publishing | 4 | Kill the process at a breakpoint, or fault injection in a test |

## Rules for flaky tests

A test that sometimes fails is a bug, usually a race in the code or the test. Do
not retry it into passing. Run concurrency tests many times in a row (for example
with JUnit's `@RepeatedTest(50)`) before trusting them.
