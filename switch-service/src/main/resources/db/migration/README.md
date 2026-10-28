# Flyway migrations

Put schema changes here as `V<number>__<description>.sql`, for example
`V1__ledger_accounts.sql`. Flyway runs them in order on startup and records each
one in `flyway_schema_history`.

Rules worth keeping:

- **Never edit a migration that has been applied anywhere.** Flyway checksums each
  file and refuses to start if an applied one changes. Fix mistakes with a new
  migration.
- **One concern per file.** Small migrations are easier to review and to explain.
- **Let the database enforce what it can.** Constraints such as `NOT NULL`,
  `UNIQUE`, `CHECK` and foreign keys are part of your design. Decide which
  invariants live here and which live in Java, and write that down in
  `docs/design/data-model.md`.

The first migrations are your week 1 data model. This folder is empty until then,
which Flyway accepts.
