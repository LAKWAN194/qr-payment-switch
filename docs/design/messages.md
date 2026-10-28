# Kafka message contracts

Status: **not started** (week 4). Decisions it depends on: ADR-008 (event
publishing), ADR-009 (topics, keys and contracts).

## Topics

| Topic | Producer | Consumers | Key | Partitions | Retention |
| --- | --- | --- | --- | --- | --- |
| | | | | | |

## Messages

For each message type: its fields and types, an example, and what the consumer
does with it.

### Example: `<message name>`

```json
{
}
```

## Delivery guarantees

<!-- At least once from the outbox means duplicates will happen.
     How does each consumer recognise and drop one? What ordering does it need,
     and does the key choice guarantee it? -->

## Versioning

<!-- How will you change a message without breaking the other side?
     (Optional fields, a version field, a new topic, a schema registry.) -->
