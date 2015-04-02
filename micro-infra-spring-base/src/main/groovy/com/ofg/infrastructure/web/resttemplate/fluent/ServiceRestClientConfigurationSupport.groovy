package com.ofg.infrastructure.web.resttemplate.fluent

import com.codahale.metrics.MetricRegistry
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.nurkiewicz.asyncretry.AsyncRetryExecutor
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.infrastructure.discovery.ServiceResolver
import com.ofg.infrastructure.web.resttemplate.MetricsAspect
import com.ofg.infrastructure.web.resttemplate.custom.RestTemplate
import com.ofg.infrastructure.web.resttemplate.fluent.config.RestClientConfigurer
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.client.RestOperations

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadFactory

/**
 * This is the main class providing the configuration for ServiceRestClient.
 * It's imported when using {@link EnableServiceRestClient}
 *
 * @since 0.8.17
 */
@CompileStatic
class ServiceRestClientConfigurationSupport {

    @Value('${rest.client.connectionTimeout:-1}')
    int connectionTimeoutMillis
    @Value('${rest.client.readTimeout:-1}')
    int readTimeoutMillis
    @Value('${rest.client.maxLogResponseChars:4096}')
    int maxLogResponseChars
    @Value('${retry.threads:10}')
    int retryPoolThreads

    private RestClientConfigurer configurer

    @Bean
    ServiceRestClient serviceRestClient(ServiceResolver serviceResolver, ServiceConfigurationResolver configurationResolver) {
        return new ServiceRestClient(microInfraSpringRestTemplate(), serviceResolver, configurationResolver)
    }

    @Bean
    RestOperations microInfraSpringRestTemplate() {
        RestClientConfigurer configurer = getRestClientConfigurer()
        RestTemplate restTemplate = new RestTemplate(configurer.maxLogResponseChars)
        this.configureMessageConverters(restTemplate.messageConverters)
        restTemplate.requestFactory = requestFactory()
        return restTemplate
    }

    private RestClientConfigurer getRestClientConfigurer() {
        if (this.configurer == null) {
            this.configurer = new RestClientConfigurer()
            createDefaultConfigurerValues(configurer)
            configureRestClientParams(configurer)
        }
        return this.configurer
    }

    private void createDefaultConfigurerValues(RestClientConfigurer configurer) {
        ClientHttpRequestFactory requestFactory = configurer.getRequestFactory()
        if (requestFactory) {
            requestFactory.setConnectTimeout(connectionTimeoutMillis)
            requestFactory.setReadTimeout(readTimeoutMillis)
        }
        configurer.setMaxLogResponseChars(maxLogResponseChars)
    }

    @Bean
    ClientHttpRequestFactory requestFactory() {
        RestClientConfigurer restClientConfigurer = getRestClientConfigurer()
        return new BufferingClientHttpRequestFactory(restClientConfigurer.requestFactory)
    }

    @Bean
    AsyncRetryExecutor retryExecutor() {
        return new AsyncRetryExecutor(retryExecutorService())
    }

    private ScheduledExecutorService retryExecutorService() {
        return Executors.newScheduledThreadPool(retryPoolThreads, retryThreadFactory())
    }

    private ThreadFactory retryThreadFactory() {
        new ThreadFactoryBuilder()
                .setNameFormat(AsyncRetryExecutor.simpleName + "-%d")
                .build()
    }

    @Bean
    MetricsAspect metricsAspect(MetricRegistry metricRegistry) {
        return new MetricsAspect(metricRegistry)
    }

    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

    }

    protected void configureRestClientParams(RestClientConfigurer configurer) {

    }
}
