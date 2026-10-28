/**
 * EMV merchant-presented QR payloads: building, parsing and validating them (TLV encoding and the CRC in data object "63").
 *
 * <p>This is the project's <b>kernel</b>. Keep it free of Spring, JPA and I/O so it can be
 * tested in isolation and explained on a whiteboard; {@code KernelIsolationTest} enforces that.
 */
package io.github.lakwan194.qrswitch.qr;
