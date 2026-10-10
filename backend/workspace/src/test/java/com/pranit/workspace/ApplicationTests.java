package com.pranit.workspace;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;

@SpringBootTest
class ApplicationTests {

    private final static Logger LOGGER = LoggerFactory.getLogger(ApplicationTests.class);

    @Test
    void contextLoads() {
        String cyan = "\u001B[36m";
        String reset = "\u001B[0m";
        String green = "\u001B[32m";
        ApplicationModules modules = ApplicationModules.of(Application.class);
        modules.forEach(module ->
                LOGGER.info("{} Module: {} {}", cyan, module.getDisplayName(), reset));
        modules.verify();
        LOGGER.info("{} ✓ All modules verified successfully! {}", green, reset);
    }

}
