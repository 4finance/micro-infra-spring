package com.ofg.infrastructure.web.resttemplate.fluent.config

import org.springframework.web.client.RestOperations

/**
 * A convenience {@link com.ofg.infrastructure.web.resttemplate.fluent.config.ServiceRestClientConfigurer ServiceRestClientConfigurer} that
 * implements all methods so that the defaults are used. Provides a backward compatible alternative of implementing
 * {@link com.ofg.infrastructure.web.resttemplate.fluent.config.ServiceRestClientConfigurer ServiceRestClientConfigurer}.
 *
 * @since 0.8.3
 */
class ServiceRestClientConfigurerSupport implements ServiceRestClientConfigurer {

    @Override
    RestOperations getRestTemplate() {
        return null
    }
}
