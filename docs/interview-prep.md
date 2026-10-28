# Interview preparation

Sri Lankan intern and graduate interviews spend most of their time on your own
projects. This file is where you get ready for that, from week 1 onwards.

## Decisions you must defend

These questions come from the project plan. Answer each in your own words in the
decision log below. Two or three sentences is enough; you should be able to say
them aloud without reading.

1. Why does a ledger need double entry?
2. What happens when the fraud scorer times out?
3. Why the outbox pattern instead of publishing to Kafka inside the transaction?
4. How does an idempotency key behave under two concurrent requests?
5. Why is the fraud threshold where it is, and what does a false positive cost a merchant?
6. Why is the QR checksum computed over `6304` as well?
7. What happens to a payment when the issuer never replies?
8. How do you stop one merchant from reading another's payments?
9. Why Kafka (Redpanda) here, and what would you lose with direct HTTP calls to the issuers?
10. What would you change to run this for real, at LANKAQR's volume?

## Decision log

Add a row whenever you make a choice someone might question. Aim for 10–15 by the
end. Link the ADR when there is one.

| Date | Question | My answer | ADR |
| --- | --- | --- | --- |
| | | | |

## Five-minute walkthrough

Practise this until it fits in five minutes without slides.

1. **The problem** (30 s): what a switch does, and why correctness under failure
   is the hard part.
2. **The architecture** (1 min): the components and how a payment flows through
   them. Be ready to draw it.
3. **One deep part** (2 min): pick the ledger, idempotency or the outbox, and go
   into detail.
4. **A failure you caused on purpose** (1 min): what broke, what you learned, what
   you changed. Use a postmortem.
5. **The numbers** (30 s): p95 latency, throughput, fraud precision and recall.

## Whiteboard sketch

Without notes, you should be able to draw:

- [ ] The containers and arrows: dashboard, switch, PostgreSQL, Kafka, two
      issuers, scorer, Redis, merchant webhook endpoint
- [ ] The payment state machine
- [ ] The journal entries for one payment, from authorisation to settlement
- [ ] The outbox flow, including what happens when the relay crashes
- [ ] The CRC loop and the TLV parsing loop

## "Tell me about a hard problem"

Use one postmortem per answer, in this shape:

- **Situation:** what you were building.
- **Problem:** what went wrong, and how you noticed.
- **What you did:** how you found the cause, and the fix.
- **Result:** the test or measurement that proves it is fixed, and what you would
  do differently.
