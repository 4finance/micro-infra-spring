package com.ofg.infrastructure.web.resttemplate.fluent

import com.codahale.metrics.MetricRegistry
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.nurkiewicz.asyncretry.AsyncRetryExecutor
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.infrastructure.discovery.ServiceResolver
import com.ofg.infrastructure.metrics.config.EnableMetrics
import com.ofg.infrastructure.web.resttemplate.MetricsAspect
import com.ofg.infrastructure.web.resttemplate.custom.RestTemplate
import groovy.transform.CompileStatic
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
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
class ServiceRestClientConfiguration {

    @Bean
    ServiceRestClient serviceRestClient(ServiceResolver serviceResolver, ServiceConfigurationResolver configurationResolver) {
        return new ServiceRestClient(microInfraSpringRestTemplate(), serviceResolver, configurationResolver)
    }

    @Bean
    RestOperations microInfraSpringRestTemplate() {
        return new RestTemplate()
    }

    @Bean
    AsyncRetryExecutor retryExecutor(@Value('${retry.threads:10}') int retryPoolThreads) {
        return new AsyncRetryExecutor(retryExecutorService(retryPoolThreads))
    }

    private ScheduledExecutorService retryExecutorService(@Value('${retry.threads:10}') int retryPoolThreads) {
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

    @Bean
    static RestTemplateAutowireCandidateFalsePostProcessor disableMicroInfraSpringRestTemplateAutowiring() {
        return new RestTemplateAutowireCandidateFalsePostProcessor()
    }

    static class RestTemplateAutowireCandidateFalsePostProcessor implements BeanFactoryPostProcessor, Ordered {

        @Override
        void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            beanFactory.getBeanDefinition("microInfraSpringRestTemplate").autowireCandidate = false
        }

        @Override
        int getOrder() {
            return HIGHEST_PRECEDENCE
        }
    }
}
