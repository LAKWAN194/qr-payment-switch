package io.github.lakwan194.qrswitch.issuer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MockIssuerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockIssuerApplication.class, args);
    }
}
