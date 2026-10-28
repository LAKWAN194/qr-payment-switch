# Project brief

## The problem

LANKAQR lets a customer pay almost any Sri Lankan merchant by scanning a QR code
with a bank or wallet app. Behind each scan sits a **switch**. It takes the
payment from the payer's app and asks the payer's bank (the *issuer*) to approve
it. It records who owes whom, tells the merchant, and settles with the banks at
the end of the day.

If any of that goes wrong, money is lost or counted twice. So the hard part of a
switch is not features. It is staying correct when requests repeat, networks time
out, services crash and messages arrive twice.

This project builds a small but honest version of that switch, and puts a fraud
model on the payment path, where its speed and its failures matter.

## What it is, and what it is not

| It is | It is not |
| --- | --- |
| "LankaQR-style": built from the public EMVCo merchant-presented QR specification, which LANKAQR follows | Connected to LankaPay or LankaClear, or built from their own technical specs, which are not public |
| A switch with a real ledger, real retries and real failure handling, run on a laptop | A system that moves real money or touches real bank or card data |
| Two simulated issuer banks and a simulated payer wallet | Certified for anything (PCI DSS, CBSL licensing) |

Say this plainly in the README and in interviews. Being exact about what you built
is part of the signal.

## Who is involved

- **Merchant**: signs up, gets a QR code, sees payments and settlements, and
  receives webhooks when a payment changes state.
- **Payer** (simulated wallet): scans a QR code and pays.
- **Issuer banks** (two mock banks): approve or decline each authorisation, and
  sometimes respond slowly or not at all, on purpose.
- **Operator** (you): runs settlement and reconciliation, and reads the mismatch
  report.

## Scope

- Merchant onboarding, with EMV merchant-presented QR payloads generated and
  parsed by your own TLV and CRC code (the **kernel**).
- A payment API with **idempotency keys**, a **state machine**
  (initiated → authorised → settled, or reversed), and safe concurrent updates.
- A **double-entry ledger**: every movement of money is recorded as balanced
  postings.
- Routing authorisations to the two issuers over **Kafka**, with timeouts and
  reversals, published reliably through a **transactional outbox**.
- **Fraud scoring** on the authorisation path: an XGBoost model behind a FastAPI
  service, called through a circuit breaker, with a written fail-open or
  fail-closed policy.
- **Merchant webhooks** signed with HMAC, retried with backoff, with a
  dead-letter queue.
- A nightly **settlement and reconciliation** job that produces a mismatch report.
- A **React merchant dashboard** with login (Asgardeo OIDC).
- A **load test** with measured p95 latency, and a **security pass**.

## Non-goals

These keep the eight weeks realistic. Revisit them only once the core is solid.

- Card payments and ISO 8583 messaging.
- More than one currency.
- Real KYC, AML or sanctions screening.
- Running across several regions or data centres.

## What "done" means

Fill in the blanks as you reach the relevant week. A number you chose and
measured is worth more than a vague claim.

- **Correct:** every invariant in [testing.md](testing.md) has a test, and
  still holds during the failure drills.
- **Fast enough:** p95 authorisation latency is at most ____ ms at ____
  payments per second on a laptop, measured with k6 (week 7).
- **Useful fraud model:** precision ____ and recall ____ at the chosen threshold,
  on synthetic data held out by time (week 5).
- **Easy to run:** `docker compose --profile apps up` starts the whole system, and
  a stranger can run the demo from the README in ten minutes.
- **Explainable:** you can answer every question in
  [interview-prep.md](interview-prep.md) without notes.

## Who this is aimed at

| Employer | What they look for that this project shows |
| --- | --- |
| Sysco LABS | Java OOP, RDBMS, Kafka, CI and tests |
| IFS, CodeGen | Spring Boot, JUnit and Mockito |
| Axiata Digital Labs | Spring and Kafka |
| Rootcode | Spring Boot questions, and a live demo of your own app |
| Sampath IT Solutions | A clean, deployable repository (their process includes a take-home assignment) |
| Epic Lanka, Dialog Pay Business, PayHere, DirectPay | Payment switching, LANKAQR, webhooks, reconciliation |
| Enactor | Java microservices, with a payments product of its own |
| Pearson | Java and Spring Boot |
| WSO2 | Asgardeo or API Manager in a real project counts towards contribution points |

The source for each line is the research report,
`reports/Sri Lanka tech internships and projects.md`.

## Ground rules

1. **Design before code.** Write the data model, the API contract and the
   decision records yourself. Then get them critiqued.
2. **Hand-write the kernel** (the QR codec and CRC; see
   [design/kernel.md](design/kernel.md)), so you can write it on a whiteboard
   from memory.
3. **Write tests for invariants first**, such as "money is never created".
4. **Never merge code you cannot explain line by line.**
5. **Commit small, with your own messages.** The history is evidence of how the
   work happened.
6. **Break things on purpose** and write a short postmortem for each
   ([postmortems/](postmortems/)).
7. **Keep the decision log** in [interview-prep.md](interview-prep.md) as you go.
