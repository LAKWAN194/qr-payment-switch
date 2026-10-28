# Payment state machine

Status: **not started** (week 3). Decision it depends on: ADR-006.

The brief names the main path: initiated → authorised → settled, or reversed.
The full set of states, events and transitions is yours to design.

## States

| State | Meaning | Terminal? |
| --- | --- | --- |
| | | |

## Transitions

| From | Event | To | Who triggers it | Ledger entries posted |
| --- | --- | --- | --- | --- |
| | | | | |

## Diagram

```mermaid
stateDiagram-v2
    %% Draw your states and transitions here.
```

## Rules

<!-- What happens on an illegal transition? On a duplicate event?
     On an event for a payment that does not exist (yet)? -->

## Timeouts

<!-- Which states can time out, after how long, and what event does the timeout raise? -->
