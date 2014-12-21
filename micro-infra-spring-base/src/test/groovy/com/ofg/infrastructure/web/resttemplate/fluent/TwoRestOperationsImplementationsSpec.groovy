package com.ofg.infrastructure.web.resttemplate.fluent

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MvcCorrelationIdSettingIntegrationSpec
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.infrastructure.discovery.ServiceResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.core.io.Resource
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestOperations

import java.lang.reflect.InvocationHandler

@ContextConfiguration(classes = Config, loader = SpringApplicationContextLoader)
class TwoRestOperationsImplementationsSpec extends MvcCorrelationIdSettingIntegrationSpec {

    @Autowired
    ComponentWithTwoRestOperationsImplementations componentWithTwoRestOperationsImplementations

    def "should allow to create additional, custom RestOperations implementation when there is already one registered in Spring context"() {
        expect:
            componentWithTwoRestOperationsImplementations.hasDependenciesInjectedCorrectly()
    }

    @Configuration
    @Import(BaseConfiguration)
    @EnableServiceRestClient
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    static class Config {

        @Bean
        RestOperations customRestOperationsImplementation() {
            return new TestRestTemplate()
        }

        @Bean
        ServiceConfigurationResolver serviceConfigurationResolver(@Value('${microservice.config.file:classpath:microservice.json}') Resource microserviceConfig) {
            return new ServiceConfigurationResolver(microserviceConfig.inputStream.text)
        }

        @Bean
        ComponentWithTwoRestOperationsImplementations componentWithTwoRestOperationsImplementations(RestOperations restOperations,
                                                                                                    ServiceRestClient serviceRestClient) {
            return new ComponentWithTwoRestOperationsImplementations(serviceRestClient, restOperations)
        }

        @Bean
        @Primary
        ServiceResolver stubForServiceResolver() {
            final InvocationHandler handler = { inv -> throw new UnsupportedOperationException() }
            return java.lang.reflect.Proxy.newProxyInstance(TwoRestOperationsImplementationsSpec.class.classLoader, [ServiceResolver] as Class[], handler)
        }
    }

}
