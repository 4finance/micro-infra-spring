package com.ofg.infrastructure.web.resttemplate.fluent.config

import groovy.transform.CompileStatic
import org.springframework.http.converter.HttpMessageConverter

/**
 * A convenience {@link com.ofg.infrastructure.web.resttemplate.fluent.config.ServiceRestClientConfigurer ServiceRestClientConfigurer} that
 * have empty methods to override.
 *
 * @since 0.8.17
 */
@CompileStatic
class ServiceRestClientConfigurerAdapter implements ServiceRestClientConfigurer {

    @Override
    void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    }

    @Override
    void configureRestClientParams(RestClientConfigurer configurer) {

    }
}
