package io.github.lakwan194.qrswitch.issuer;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Which simulated bank this instance plays. The same jar runs twice, as BANK_A and BANK_B.
 *
 * <p>How each bank behaves (approval rules, latency, timeouts it should simulate) is part of
 * your week 4 design. Add the settings here when you decide them.
 */
@Validated
@ConfigurationProperties("issuer")
public record IssuerProperties(@NotBlank String id) {
}
