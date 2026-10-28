package io.github.lakwan194.qrswitch;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Keeps the QR kernel pure: no framework, persistence or web code inside it, so it can be
 * unit-tested in isolation and explained without the rest of the system.
 */
@AnalyzeClasses(packages = "io.github.lakwan194.qrswitch", importOptions = ImportOption.DoNotIncludeTests.class)
class KernelIsolationTest {

    @ArchTest
    static final ArchRule qrKernelHasNoFrameworkDependencies = noClasses()
            .that().resideInAPackage("..qrswitch.qr..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..", "jakarta.persistence..", "jakarta.servlet..", "org.hibernate..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule qrKernelDependsOnNothingElseInTheService = noClasses()
            .that().resideInAPackage("..qrswitch.qr..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..qrswitch.merchant..", "..qrswitch.payment..", "..qrswitch.ledger..",
                    "..qrswitch.routing..", "..qrswitch.outbox..", "..qrswitch.webhook..",
                    "..qrswitch.reconciliation..", "..qrswitch.fraud..")
            .allowEmptyShould(true);
}
