package io.github.lakwan194.qrswitch.issuer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "issuer.id=BANK_B")
@AutoConfigureMockMvc
class MockIssuerApplicationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    IssuerProperties issuer;

    @Test
    void takesItsIdentityFromConfiguration() {
        assertThat(issuer.id()).isEqualTo("BANK_B");
    }

    @Test
    void reportsHealthyAndWhichBankItIs() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
        mvc.perform(get("/actuator/info")).andExpect(status().isOk())
                .andExpect(jsonPath("$.issuer").value("BANK_B"));
    }
}
