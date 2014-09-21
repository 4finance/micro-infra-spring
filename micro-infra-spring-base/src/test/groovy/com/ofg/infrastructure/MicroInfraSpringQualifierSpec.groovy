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
    ComponentWithMultipleDependencies componentWithMultipleDependencies

    def "Application can create its own RestOperations without NoUniqueBeanDefinitionException being raised"() {
        expect:
            componentWithMultipleDependencies.hasDependenciesInjectedCorrectly()
    }

    @Configuration
    static class Config {

        @Autowired
        ServiceRestClient serviceRestClient

        @Bean
        @Qualifier("CustomApplicationQualifier")
        //Application creates its own RestTemplate
        RestOperations applicationRestOperations() {
            return new RestTemplate()
        }

        @Bean
        //Application has a component using both: its own RestOperations and ServiceRestClient
        ComponentWithMultipleDependencies componentWithMultipleDependencies() {
            return new ComponentWithMultipleDependencies(serviceRestClient, applicationRestOperations())
        }

        @Bean
        //Stubbing ServiceResolver (it's required by ServiceRestClient)
        ServiceResolver serviceResolver() {
            [:] as ServiceResolver
        }
    }

}
