/**
 * The transactional outbox: recording events in the same database transaction as the state change,
 * then publishing them reliably.
 *
 * <p>Week 4. See {@code docs/domain/payments-primer.md} for why this pattern exists.
 */
package io.github.lakwan194.qrswitch.outbox;
