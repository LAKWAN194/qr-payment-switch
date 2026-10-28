# Data model

Status: **not started** (week 1). Decisions it depends on: ADR-002 (money),
ADR-003 (ledger).

## Entities

<!-- One line each: what it is, and its natural key.
     Likely candidates: merchant, QR code, payment, account, journal entry,
     posting, idempotency record, outbox message, webhook delivery,
     settlement batch. Add, merge or drop as your design needs. -->

## Diagram

<!-- Replace with your own. GitHub renders Mermaid erDiagram blocks. -->

```mermaid
erDiagram
    %% Draw your tables and relationships here.
```

## Invariants and where they are enforced

Write each invariant as a statement a test can check, then say which layer
enforces it. Enforcing in the database means a bug in Java cannot break it.

| Invariant | Database (constraint, trigger) | Java | Test |
| --- | --- | --- | --- |
| Every journal entry balances (debits = credits) | | | |
| Postings are never updated or deleted | | | |
| An idempotency key maps to exactly one request | | | |
| | | | |

## Indexes

<!-- Which queries must be fast (the authorisation path, the dashboard lists,
     reconciliation), and which index serves each. -->

## Retention

<!-- What is kept forever (ledger), what expires (idempotency records?),
     and how expiry happens. -->

## Open questions

-
