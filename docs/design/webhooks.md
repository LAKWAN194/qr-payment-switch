# Merchant webhooks

Status: **not started** (week 6). Decisions it depends on: ADR-013 (signing),
ADR-014 (retries and dead letters).

## Events

| Event | When it is sent | Payload fields |
| --- | --- | --- |
| | | |

## Request format

<!-- Method, headers, body. An example request with a real signature,
     computed by a script you include in the repo. -->

## Signature

<!-- What exactly is signed (body only? timestamp + body?), the algorithm,
     how the secret is created, stored and rotated, and the tolerance window. -->

## Verifying a webhook (merchant side)

<!-- Step-by-step instructions a merchant developer could follow, with a code sample. -->

## Retries

<!-- Which responses count as success, the backoff schedule, the maximum attempts,
     and what happens after the last one. -->

## Dead-letter queue

<!-- Where failed events go, who can see them, and how they are replayed. -->
