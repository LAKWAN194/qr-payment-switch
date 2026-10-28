package io.github.lakwan194.qrswitch.qr;

/**
 * The checksum carried in data object "63" of an EMV merchant-presented QR payload.
 *
 * <p><b>Kernel:</b> write this by hand. See {@code docs/design/kernel.md} for the rules and
 * {@code docs/domain/emv-qr-mpm.md} for the spec notes. The tests in {@code Crc16Test} are
 * disabled until you implement it; enable them, then add your own.
 */
public final class Crc16 {

    private Crc16() {
    }

    /**
     * Computes CRC-16/CCITT-FALSE over {@code data}: polynomial 0x1021, initial value 0xFFFF,
     * no input or output reflection, no final XOR.
     *
     * @return the checksum in the low 16 bits
     */
    public static int ccittFalse(byte[] data) {
        throw new UnsupportedOperationException("Kernel not implemented yet: see docs/design/kernel.md");
    }
}
