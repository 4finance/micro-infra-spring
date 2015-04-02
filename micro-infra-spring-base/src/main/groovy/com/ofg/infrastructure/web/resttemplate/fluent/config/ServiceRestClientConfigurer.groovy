package com.ofg.infrastructure.web.resttemplate.fluent.config

import org.springframework.http.converter.HttpMessageConverter

/**
 * Interface to be implemented by Spring beans willing to provide their own configuration for
 * {@link com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClientConfiguration ServiceRestClientConfiguration}.
 *
 * <p>Consider using {@link com.ofg.infrastructure.web.resttemplate.fluent.config.ServiceRestClientConfigurerAdapter ServiceRestClientConfigurerAdapter}
 * providing default implementations for all components. Only custom configuration needs to be overridden. Furthermore, backward compatibility
 * of this interface will be insured in case new customization options are introduced in the future.
 *
 * <p>See {@link com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClientConfiguration ServiceRestClientConfiguration}.
 *
 * @since 0.8.17
 */
interface ServiceRestClientConfigurer {

    void configureMessageConverters(List<HttpMessageConverter<?>> converters);

    void configureRestClientParams(RestClientConfigurer configurer);
}
