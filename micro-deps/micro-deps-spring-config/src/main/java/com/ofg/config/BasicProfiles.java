package com.ofg.config;

import java.util.Arrays;
import java.util.List;

/**
 * 4finance default Spring profiles
 */
public class BasicProfiles {

    /**
     * Profile used for development. Starts Zookeeper and stubs.
     */
    public static final String DEVELOPMENT = "dev";
    /**
     * Profile used for any environment to run microservice in production mode.
     */
    public static final String PRODUCTION = "prod";
    /**
     * Profile used for integration and unit tests
     */
    public static final String TEST = "test";
    /**
     * Third party services should be mocked in smokeTests profile to guarantee pipeline stability
     */
    public static final String SMOKE_TESTS = "smokeTests";

    public static final String SPRING_CLOUD = "springCloud";

    public static List<String> all() {
        return Arrays.asList(DEVELOPMENT, PRODUCTION, TEST, SMOKE_TESTS, SPRING_CLOUD);
    }
}
