# Security checklist

Work through this in week 8, but design with it from week 1: most of these are
cheaper to prevent than to fix. For each item, record what you checked, what you
found, and the test that now guards it. Your web-security assignment (DOM XSS,
NoSQL injection) is directly reusable here.

The OWASP API Security Top 10 (2023) is the reference list; the items below are the
ones that matter most for this system.

## Access control

- [ ] **Broken object level authorisation (IDOR).** Can merchant A read or change
      merchant B's payments by changing an ID in the URL? Test every endpoint that
      takes an ID, with a token for the wrong merchant.
- [ ] **Function level authorisation.** Can a merchant call operator endpoints
      (settlement, reconciliation, dead-letter replay)?
- [ ] **Mass assignment.** Can a request set fields it should not, such as a status
      or an amount after creation?

## Input handling

- [ ] **SQL injection.** All queries are parameterised: JPA or `JdbcTemplate`
      with bind parameters, never string concatenation.
- [ ] **QR payload input.** The parser handles hostile input: huge lengths, wrong
      lengths, non-ASCII, a missing CRC. The property-based tests help here.
- [ ] **Validation.** Amounts are positive, have the right scale and stay under a
      maximum. Currency and country are what the system supports.

## Webhooks and messages

- [ ] **Webhook replay.** An old, correctly signed webhook is rejected (timestamp
      tolerance), and a duplicate is ignored (event ID).
- [ ] **Signature comparison** uses a constant-time comparison, not `equals`.
- [ ] **Server-side request forgery (SSRF).** A merchant's webhook URL cannot point
      at internal addresses (`localhost`, `169.254.169.254`, private ranges).
- [ ] **Kafka consumers** reject messages they cannot parse into a dead-letter
      topic instead of crashing or looping.

## Secrets and data

- [ ] No secrets in the repository. `.env` is ignored; `.env.example` holds only
      local placeholders. Scan the history before publishing.
- [ ] Webhook secrets are stored hashed or encrypted, and can be rotated.
- [ ] Logs contain no secrets, tokens or full phone numbers.

## Abuse and availability

- [ ] **Rate limiting** on payment creation and login, per merchant or client.
- [ ] **Resource limits:** request body size, page sizes on list endpoints,
      timeouts on every outbound call.

## Findings

| Date | Finding | Severity | Fix | Test |
| --- | --- | --- | --- | --- |
| | | | | |
