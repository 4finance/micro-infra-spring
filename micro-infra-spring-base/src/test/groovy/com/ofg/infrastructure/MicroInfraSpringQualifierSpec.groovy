package com.ofg.infrastructure

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MvcIntegrationSpec
import com.ofg.infrastructure.discovery.ServiceResolver
import com.ofg.infrastructure.web.resttemplate.custom.RestTemplateConfiguration
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClientConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestOperations
import org.springframework.web.client.RestTemplate

@ContextConfiguration(classes = [Config, BaseConfiguration, RestTemplateConfiguration, ServiceRestClientConfiguration],
        loader = SpringApplicationContextLoader)
class MicroInfraSpringQualifierSpec extends MvcIntegrationSpec {

    @Autowired
    ComponentWithTwoDifferentRestOperationsImplementations componentWithMultipleDependencies

    def "should allow to create additional, custom RestOperations implementation when there is already one registered in Spring context"() {
        expect:
            componentWithMultipleDependencies.hasDependenciesInjectedCorrectly()
    }

    @Configuration
    static class Config {

        @Autowired
        ServiceRestClient serviceRestClient

        @Bean
        @Qualifier("CustomApplicationQualifier")
        RestOperations customRestOperationsImplementation() {
            return new RestTemplate()
        }

        @Bean
        ComponentWithTwoDifferentRestOperationsImplementations componentWithTwoDifferentRestOperationsImplementations() {
            return new ComponentWithTwoDifferentRestOperationsImplementations(serviceRestClient, customRestOperationsImplementation())
        }

        @Bean
        ServiceResolver stubForServiceResolver() {
            [:] as ServiceResolver
        }
    }

}
