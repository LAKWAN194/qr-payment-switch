# Architecture decision records

An ADR records one decision: the situation, the options, what you chose and why,
and what it costs. Short is fine. Its value is that you wrote the reasoning down
**before** building, so in an interview you can say why, not only what.

## How to write one

1. Copy [0000-template.md](0000-template.md) to `NNNN-short-title.md`, for
   example `0002-money-representation.md`.
2. Fill it in with status **Proposed**. Get it critiqued.
3. When you have decided, change the status to **Accepted** and update the table
   below.
4. Never rewrite an accepted ADR. If you change your mind, write a new one that
   **supersedes** it, and mark the old one **Superseded by NNNN**.

## Backlog

These are the decisions this project needs, in the order the roadmap needs them.
Each lists the main options and what to weigh, but **none is decided**. You may
find others; add them.

| ADR | Decision | Due | Status |
| --- | --- | --- | --- |
| 001 | Service boundaries | Week 1 | Open |
| 002 | Money representation | Week 1 | Open |
| 003 | Ledger design | Week 1 | Open |
| 004 | Merchant identity and QR types | Week 2 | Open |
| 005 | Idempotency keys | Week 3 | Open |
| 006 | State machine implementation | Week 3 | Open |
| 007 | Concurrency control | Week 3 | Open |
| 008 | Reliable event publishing | Week 4 | Open |
| 009 | Topics, keys and message contracts | Week 4 | Open |
| 010 | Issuer timeouts and reversals | Week 4 | Open |
| 011 | Fraud scorer failure policy | Week 5 | Open |
| 012 | Fraud threshold | Week 5 | Open |
| 013 | Webhook signing | Week 6 | Open |
| 014 | Retries and dead letters | Week 6 | Open |
| 015 | Settlement and reconciliation | Week 6 | Open |
| 016 | Authentication and authorisation | Week 7 | Open |

### 001: Service boundaries

How is the switch split? Options: one service with clear internal packages (a
"modular monolith", which is how the skeleton is laid out), or separate
services for payments, ledger and webhooks. Weigh: transactions across modules
(one database transaction is far simpler than a distributed one), how much you can
build in eight weeks, and what you can explain. Tools such as Spring Modulith or
ArchUnit can enforce module boundaries inside one service.

### 002: Money representation

`BigDecimal` with a fixed scale, or a `long` count of cents? `NUMERIC(p, s)` or
`BIGINT` in PostgreSQL? A string or a number in JSON? Weigh: exactness, how each
layer serialises it, rounding rules, and how easy the wrong choice is to misuse.
Read [payments-primer.md §1](../domain/payments-primer.md#1-money-and-rounding).

### 003: Ledger design

Which accounts exist, which entries each payment state posts, and how balances
are read. Options for balances: sum postings on every read, keep a running
balance updated in the same transaction, or take periodic snapshots. Weigh:
auditability, read speed, and how the "debits equal credits" rule is enforced.
Read [§2](../domain/payments-primer.md#2-double-entry-bookkeeping).

### 004: Merchant identity and QR types

What identifies a merchant inside your merchant account template, and what is
your globally unique identifier? Static codes only, or dynamic codes with amounts
too? Weigh: what the dashboard needs, and how a payment is matched to a merchant.

### 005: Idempotency keys

Where keys are stored (a PostgreSQL table with a unique constraint, Redis
`SET NX`, or both), how long they live, what is stored with them (the request
fingerprint, the response), and what a concurrent duplicate receives (wait, `409`,
or the original response). Weigh: correctness when the cache and the database
disagree, and what happens during a crash between steps. Read
[§3](../domain/payments-primer.md#3-idempotency-keys).

### 006: State machine implementation

An enum with a transition table, a database table of allowed transitions, or a
library such as Spring Statemachine. Weigh: how easily the table can be tested
and read, and whether the rules must change without a deploy. Read
[§4](../domain/payments-primer.md#4-state-machines).

### 007: Concurrency control

Optimistic locking (`@Version`) or `SELECT … FOR UPDATE`, and what the loser of a
conflict does. Weigh: how often conflicts really happen, lock hold times, and
deadlock risk. Read [§5](../domain/payments-primer.md#5-concurrency-control).

### 008: Reliable event publishing

Outbox polled by the service, outbox read through change data capture (Debezium),
or Kafka transactions. Weigh: operational complexity, delivery latency,
ordering, and what you can run on a laptop. Read
[§6](../domain/payments-primer.md#6-the-transactional-outbox).

### 009: Topics, keys and message contracts

Which topics, what each message contains, and what the partition key is (payment
ID, merchant ID, issuer). Weigh: ordering guarantees per key, how consumers drop
duplicates, and how messages evolve. Fill in [design/messages.md](../design/messages.md).

### 010: Issuer timeouts and reversals

The authorisation timeout, the reversal retry schedule, and what the payer and
merchant see while a payment is unknown. Weigh: customer experience against the
risk of a payment approved at the bank but unknown to the switch. Read
[§7](../domain/payments-primer.md#7-timeouts-and-reversals).

### 011: Fraud scorer failure policy

Fail open, fail closed, or a split (for example by amount). The latency budget,
the circuit breaker settings, and the fallback. Weigh: fraud loss against lost
sales while the scorer is down. Read
[§8](../domain/payments-primer.md#8-fraud-scoring-on-the-authorisation-path).

### 012: Fraud threshold

Where on the precision-recall curve to operate, and why. Weigh: the cost of a
false positive (an honest customer declined) against a false negative (fraud let
through). State the costs you assumed.

### 013: Webhook signing

What is signed, the algorithm, the header format, the timestamp tolerance, and how
secrets are issued and rotated. Read
[§9](../domain/payments-primer.md#9-webhooks).

### 014: Retries and dead letters

The backoff schedule (base, factor, jitter, cap), the maximum attempts, where dead
letters go, and how they are replayed. The same thinking applies to Kafka consumer
failures.

### 015: Settlement and reconciliation

When settlement runs, what each issuer reports, what counts as a match, and what
happens to each kind of mismatch. Read
[§10](../domain/payments-primer.md#10-settlement-and-reconciliation).

### 016: Authentication and authorisation

How merchants log in to the dashboard (Asgardeo OIDC, Keycloak, or Spring
Authorization Server), how the API checks tokens, and how one merchant is stopped
from reading another's data. Asgardeo is a WSO2 product, so using it also counts
towards WSO2 contribution points.
