# Load tests (week 7)

[k6](https://grafana.com/docs/k6/latest/) scripts. `smoke.js` only checks that k6
can reach the switch; run it with `make load-smoke` while the switch is up.

The week 7 test sends real payment traffic (with fresh idempotency keys, and some
deliberate retries) and records p95 latency and throughput. Put the results in the
README, with the machine they were measured on, and use them to fill in the
"fast enough" line in `docs/brief.md`.
