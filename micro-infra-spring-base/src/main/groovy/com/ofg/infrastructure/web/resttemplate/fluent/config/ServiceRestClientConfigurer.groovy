package com.ofg.infrastructure.web.resttemplate.fluent.config

import org.springframework.web.client.RestOperations

/**
 * Interface to be implemented by Spring beans willing to provide their own components for
 * {@link com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClientConfiguration ServiceRestClientConfiguration}.
 *
 * <p>Consider using {@link com.ofg.infrastructure.web.resttemplate.fluent.config.ServiceRestClientConfigurerSupport ServiceRestClientConfigurerSupport}
 * providing default implementations for all components. Only custom components needs to be overridden. Furthermore, backward compatibility
 * of this interface will be insured in case new customization options are introduced in the future.
 *
 * <p>See {@link com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClientConfiguration ServiceRestClientConfiguration}.
 *
 * @since 0.8.3
 */
interface ServiceRestClientConfigurer {

    RestOperations getRestTemplate()

    //Other important beans...
}
