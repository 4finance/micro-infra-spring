package com.ofg.infrastructure.hystrix

import com.ofg.infrastructure.config.PropertyAbsentOrEnabledCondition
import groovy.transform.CompileStatic
import groovy.transform.PackageScope

/**
 * Checks for the presence of the property to verify whether Hystrix is enabled or not
 */
@CompileStatic
@PackageScope
class IsHystrixServletEnabled extends PropertyAbsentOrEnabledCondition {

    public static final String HYSTRIX_SERVLET_ENABLED = 'com.ofg.infra.microservice.hystrix.servlet'

    IsHystrixServletEnabled() {
        super(HYSTRIX_SERVLET_ENABLED)
    }

}
