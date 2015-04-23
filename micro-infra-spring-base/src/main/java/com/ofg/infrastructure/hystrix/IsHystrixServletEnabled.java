package com.ofg.infrastructure.hystrix;

import com.ofg.infrastructure.config.PropertyAbsentOrEnabledCondition;

/**
 * Checks for the presence of the property to verify whether Hystrix is enabled or not
 */
class IsHystrixServletEnabled extends PropertyAbsentOrEnabledCondition {

    private static final String HYSTRIX_SERVLET_ENABLED = "com.ofg.infra.microservice.hystrix.servlet";

    public IsHystrixServletEnabled() {
        super(HYSTRIX_SERVLET_ENABLED);
    }
}
