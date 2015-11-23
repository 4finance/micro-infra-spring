package com.ofg.infrastructure.web.resttemplate.fluent

import com.ofg.infrastructure.discovery.ServiceResolver
import com.ofg.infrastructure.metrics.config.EnableMetrics
import com.ofg.infrastructure.tracing.EnableTracing
import com.ofg.infrastructure.web.resttemplate.fluent.config.RestClientConfigurer
import com.ofg.infrastructure.web.resttemplate.fluent.config.ServiceRestClientConfigurer
import groovy.transform.CompileStatic
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestOperations

/**
 * Creates a bean of abstraction over {@link RestOperations}.
 *
 * @see ServiceRestClient
 * @see ServiceResolver
 */
@Configuration
@CompileStatic
@EnableMetrics
@EnableTracing
class ServiceRestClientConfiguration extends ServiceRestClientConfigurationSupport {

    @Autowired(required = false)
    private ServiceRestClientConfigurer serviceRestClientConfigurer

    @Autowired(required = false)
    List<ClientHttpRequestInterceptor> interceptors

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

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        if (serviceRestClientConfigurer) {
            serviceRestClientConfigurer.configureMessageConverters(converters)
        }
    }

    @Override
    protected void configureRestClientParams(RestClientConfigurer configurer) {
        if (serviceRestClientConfigurer) {
            serviceRestClientConfigurer.configureRestClientParams(configurer)
        }
    }
}
