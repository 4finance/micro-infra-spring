package com.ofg.infrastructure.web.resttemplate.fluent

import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.infrastructure.discovery.ServiceResolver
import com.ofg.infrastructure.web.resttemplate.custom.RestTemplate
import groovy.transform.CompileStatic
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.web.client.RestOperations

/**
 * Creates a bean of abstraction over {@link RestOperations}.
 * 
 * @see ServiceRestClient
 * @see ServiceResolver
 */
@Configuration
@CompileStatic
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
