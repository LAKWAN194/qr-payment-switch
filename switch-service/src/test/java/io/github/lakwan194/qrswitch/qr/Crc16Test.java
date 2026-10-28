package io.github.lakwan194.qrswitch.qr;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Known answers for CRC-16/CCITT-FALSE. Remove {@code @Disabled} once {@link Crc16} is
 * implemented. These are only spec facts; the property-based tests are yours to write
 * (see {@code docs/testing.md}).
 */
@Disabled("Kernel not implemented yet: see docs/design/kernel.md")
class Crc16Test {

    private static int crc(String ascii) {
        return Crc16.ccittFalse(ascii.getBytes(StandardCharsets.US_ASCII));
    }

    @Test
    void matchesTheStandardCheckValue() {
        // The CRC catalogue's check value for this algorithm (CRC-16/IBM-3740).
        assertThat(crc("123456789")).isEqualTo(0x29B1);
    }

    @Test
    void emptyInputReturnsTheInitialValue() {
        assertThat(crc("")).isEqualTo(0xFFFF);
    }

    @Test
    void matchesTheChecksumOfTheStaticSampleQr() {
        // Everything up to and including "6304", the ID and length of the CRC object itself.
        String withoutCrc = "00020101021126430028io.github.lakwan194.qrswitch0107M000123"
                + "5204581253031445802LK5916Kandy Brew House6005Kandy6304";
        assertThat(crc(withoutCrc)).isEqualTo(0xBF84);
    }
}
