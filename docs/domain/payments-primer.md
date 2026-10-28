# Payments primer

The patterns this project is built on, explained from first principles. Each
section ends with the questions you have to answer for this system. The answers
go in decision records ([adr/README.md](../adr/README.md)), not here.

## 1. Money and rounding

Money must be exact. Binary floating point (`double`, `float`) cannot represent
most decimal fractions exactly: `0.1 + 0.2` is not `0.3`. Real systems store
money either as a decimal type (`BigDecimal` in Java, `NUMERIC` in PostgreSQL) or
as an integer count of the smallest unit (cents). Each choice has costs: decimals
need an agreed scale and rounding mode, integers need every reader to know the
unit.

**Questions:** How is an amount represented in Java, in PostgreSQL, in JSON and
in Kafka messages? Where can rounding happen, and which rounding mode applies?
(ADR-002)

## 2. Double-entry bookkeeping

A single "balance" column that you add to and subtract from loses history and
cannot be audited. Double-entry bookkeeping records every movement of money as a
**journal entry** made of two or more **postings**. Each posting debits or
credits one **account**. The rule that makes it work:

> In every journal entry, total debits equal total credits.

So money is never created or destroyed inside the system. It only moves between
accounts. A balance is then derived: the sum of the postings to that account.
Entries are **append-only**. A mistake is corrected with a new reversing entry,
never by editing an old one.

A QR payment touches several accounts over its life: the payer's issuer owes the
switch, the switch owes the merchant, and fees may go to the switch's own revenue
account. Settlement moves the amounts between those accounts once the banks have
actually paid.

**Questions:** Which accounts exist (per merchant, per issuer, the switch's own)?
Which entries does each state change post? How do you get a balance quickly
without losing auditability? Which of the "debits equal credits" checks does the
database enforce? (ADR-003)

## 3. Idempotency keys

Networks fail after a request is sent but before the response arrives. The client
cannot tell whether the payment happened, so it retries. Without protection, a
retry charges twice.

An **idempotency key** is a unique value the client generates and sends with the
request (often in an `Idempotency-Key` header). The server remembers each key
with the result it produced:

- Same key, same request → return the stored result. Do not repeat the work.
- Same key, **different** request → reject it, because the client has a bug.
- Two requests with the same key **at the same moment** → only one may do the
  work. This is the hard case.

Stripe's engineering blog explains the approach well (see
[references.md](../references.md)).

**Questions:** Where are keys stored, and for how long? What makes the
"same moment" case safe: a unique constraint, a lock, or something else? What does
the second concurrent request receive? (ADR-005)

## 4. State machines

A payment is always in exactly one state, and only some transitions are legal.
Writing these down as a table (state, event → next state) and checking every
change against it stops whole classes of bugs, such as settling a payment that was
reversed. Terminal states (settled, reversed, failed) never change again.

**Questions:** Which states and events exist? Which transitions are legal? What
happens when an illegal transition is attempted: an exception, a no-op, or an
alert? Where is the table: an enum, a database table, or a library? (ADR-006)

## 5. Concurrency control

Two threads updating the same payment can overwrite each other's work: the "lost
update". Two standard defences:

- **Optimistic locking:** each row has a version number. An update succeeds only
  if the version is still the one you read (`UPDATE … WHERE version = ?`).
  Otherwise you retry or fail. It is cheap when conflicts are rare.
- **Pessimistic locking:** lock the row while you work on it
  (`SELECT … FOR UPDATE`). It is simpler to reason about, but holds locks across
  your logic and can deadlock.

**Questions:** How often do conflicts really happen in this system? What does the
loser of a conflict do? (ADR-007)

## 6. The transactional outbox

The switch must update its database **and** publish a Kafka message, such as "ask
the issuer to authorise". These are two different systems, so no single
transaction covers both:

- Commit the database first, then crash before publishing: the issuer is never
  asked, and the payment is stuck.
- Publish first, then fail to commit: the issuer authorises a payment the switch
  has no record of.

The **outbox pattern** fixes this. In the *same* database transaction as the state
change, insert a row into an `outbox` table describing the message. A separate
**relay** reads unpublished rows, publishes them to Kafka, and marks them as sent.
If the relay crashes after publishing but before marking a row, the message goes
out again. So delivery is **at least once**, and every consumer must cope with
duplicates.

The relay can poll the table, or read the database's change log with a tool such
as Debezium (change data capture).

**Questions:** Polling or change data capture? How do consumers detect
duplicates? How are messages ordered for one payment? (ADR-008, ADR-009)

## 7. Timeouts and reversals

When the issuer does not answer in time, the switch must not leave the payment
pending forever. But "no answer" does not mean "declined". The issuer might have
approved it, and the reply was lost. The usual answer is to treat a timeout as
**unknown** and send a **reversal**, telling the issuer to undo the authorisation
if it made one. Reversals can also time out, so they are retried, and the issuer
must handle a reversal for an authorisation it never saw.

**Questions:** How long is the timeout, and why that long? How many reversal
attempts, how far apart? What does the merchant see meanwhile? (ADR-010)

## 8. Fraud scoring on the authorisation path

A model that runs inside a payment has two jobs: be accurate, and be **fast and
predictable**. If it is slow or down, the switch needs a rule:

- **Fail open:** approve without a score. Customers are not blocked, but fraud
  gets through while the model is down.
- **Fail closed:** decline. Fraud is blocked, but so is every honest customer.
- Something in between, such as failing open below an amount and closed above it.

A **circuit breaker** stops calling a service that keeps failing, so every
payment does not wait for a timeout. Once the circuit is open, the switch applies
its policy immediately, and checks every so often whether the service has
recovered.

The threshold is a business choice as much as a technical one. A false positive
declines an honest customer and costs the merchant a sale. A false negative costs
the money that was stolen.

**Questions:** What is the latency budget? Which failure policy, and why? How is
the threshold chosen, and what does a false positive cost? (ADR-011, ADR-012)

## 9. Webhooks

Merchants learn about payment changes through **webhooks**: HTTP POSTs from the
switch to a URL the merchant registered. Three problems to solve:

- **Authenticity:** the merchant must be able to prove a webhook came from the
  switch. The usual method is an HMAC-SHA256 signature over the body (and a
  timestamp), sent in a header, with a secret shared per merchant.
- **Replay:** an attacker who captures a webhook can resend it. A timestamp inside
  the signed content, with a tolerance window, limits this. An event ID lets the
  receiver drop duplicates.
- **Delivery:** the merchant's server is sometimes down. Retry with
  **exponential backoff and jitter** (randomised gaps that grow), give up after a
  limit, and move the event to a **dead-letter queue** where it can be inspected
  and replayed.

**Questions:** What is signed, and in what format? What is the tolerance window?
How many retries, over how long? Who can replay dead letters? (ADR-013, ADR-014)

## 10. Settlement and reconciliation

Authorisation is a promise. **Settlement** is when money actually moves between
the banks, usually in a batch at the end of the day. **Reconciliation** compares
the switch's records with each bank's records and lists every difference: a
payment one side has and the other does not, different amounts, different states.
Real switches treat the mismatch report as a core product, not an afterthought.

**Questions:** What does each mock issuer report, and in what format? What counts
as a match? What happens to each kind of mismatch? (ADR-015)
