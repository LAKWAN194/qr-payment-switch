# Your design work

Everything in this folder is yours to write. The skeleton deliberately stops here,
because these are the parts an interviewer will ask you to explain. Each file is a
template: the headings and questions are there, the answers are not.

Write each design **before** the code it describes, then get it critiqued. Change
it as you learn. A design that changed for a reason you can explain is better than
one that never changed.

| File | What it is | When |
| --- | --- | --- |
| [data-model.md](data-model.md) | Tables, keys, constraints, and which invariants the database enforces | Week 1 |
| [kernel.md](kernel.md) | The QR codec and CRC you write by hand: its API, its strictness rules, its tests | Week 2 |
| [openapi.yaml](openapi.yaml) | The HTTP contract for merchants, payments and the dashboard | Weeks 2–3, grows later |
| [state-machine.md](state-machine.md) | Payment states, events and legal transitions | Week 3 |
| [messages.md](messages.md) | Kafka topics, keys and message shapes between the switch and the issuers | Week 4 |
| [webhooks.md](webhooks.md) | What merchants receive, how it is signed, how it is retried | Week 6 |

## What a good design document has

- **The decision and the reason**, in a sentence each. Link to the ADR when the
  choice was a real trade-off.
- **The failure cases**, not only the happy path: what happens on a duplicate, a
  timeout, a crash halfway through.
- **The invariants**: statements that must always be true, written so a test can
  check them.
- **A diagram** where one helps. GitHub renders Mermaid inside Markdown code
  blocks, so diagrams can live next to the text and change with it.
- **Open questions**, listed honestly, until they are answered.

## Getting a critique

When a design is drafted, ask for a review that attacks it rather than rewrites
it. For example: "Here is my ledger design. List the ways money could be created,
lost or double-counted, and the concurrency cases I have not covered." Then decide
for yourself what to change, and record why.
