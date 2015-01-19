package com.ofg.infrastructure.web.resttemplate.fluent

import com.codahale.metrics.MetricRegistry
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.nurkiewicz.asyncretry.AsyncRetryExecutor
import com.ofg.infrastructure.discovery.EnableServiceDiscovery
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.infrastructure.discovery.ServiceResolver
import com.ofg.infrastructure.metrics.config.EnableMetrics
import com.ofg.infrastructure.web.resttemplate.MetricsAspect
import com.ofg.infrastructure.web.resttemplate.custom.RestTemplate
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestOperations

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadFactory

/**
 * Creates a bean of abstraction over {@link RestOperations}.
 *
 * @see ServiceRestClient
 * @see ServiceResolver
 */
@Configuration
@CompileStatic
@EnableMetrics
@EnableServiceDiscovery
class ServiceRestClientConfiguration {

    @Value('${rest.client.connectionTimeout:-1}') int connectionTimeoutMillis
    @Value('${rest.client.readTimeout:-1}') int readTimeoutMillis
    @Value('${rest.client.maxLogResponseChars:4096}') int maxLogResponseChars
    @Value('${retry.threads:10}') int retryPoolThreads

    @Bean
    ServiceRestClient serviceRestClient(
            @Qualifier('micro-infra-spring') RestOperations restOperations,
            ServiceResolver serviceResolver,
            ServiceConfigurationResolver configurationResolver
    ) {
        return new ServiceRestClient(restOperations, serviceResolver, configurationResolver)
    }

    /**
     * For my solution to work all micro-infra beans should:
     *    1. be candidates for autowiring
     *    2. be injected by type + qualifier within micro-infra
     *    3. have names prefixed with 'microInfra'
     *    4. be defined with @ConditionalOnMissingBean
     *
     * The above can be achieved easily by creating an ast transformation that will add
     * `@Qualifier('micro-infra')` to all method parameters (and - optionally - all @Bean definitions).
     * The transformation can also report compile errors when the bean name does not start with 'microInfra'.
     *
     * Adding @ConditionalOnMissingBean can be done either by the transformation or by using a meta-annotation
     * that will carry all of [@Bean, @Qualifier('micro-infra-spring'), @ConditionalOnMissingBean] declarations.
     */
    @Bean
    @Qualifier('micro-infra-spring')
    @ConditionalOnMissingBean
    RestOperations microInfraSpringRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(maxLogResponseChars)
        restTemplate.requestFactory = requestFactory()
        return restTemplate
    }

    @Bean
    ClientHttpRequestFactory requestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory()
        requestFactory.setConnectTimeout(connectionTimeoutMillis)
        requestFactory.setReadTimeout(readTimeoutMillis)
        return new BufferingClientHttpRequestFactory(requestFactory)
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
}
