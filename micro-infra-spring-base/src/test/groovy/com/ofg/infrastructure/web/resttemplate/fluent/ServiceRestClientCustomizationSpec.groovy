package com.ofg.infrastructure.web.resttemplate.fluent

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MvcCorrelationIdSettingIntegrationSpec
import com.ofg.infrastructure.web.resttemplate.fluent.config.ServiceRestClientConfigurer
import com.ofg.infrastructure.web.resttemplate.fluent.config.ServiceRestClientConfigurerSupport
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestOperations

@ContextConfiguration(classes = CustomConfig, loader = SpringApplicationContextLoader)
class ServiceRestClientCustomizationSpec extends MvcCorrelationIdSettingIntegrationSpec {

    @Autowired
    private ServiceRestClient serviceRestClient

    def "should allow to provide custom RestTemplate via ServiceRestClientConfigurer"() {
        expect:
            serviceRestClient.restOperations instanceof TestRestTemplate
    }

    @Configuration
    @Import(BaseConfiguration)
    @EnableServiceRestClient
    @CompileStatic
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    static class CustomConfig {
        @Bean
        ServiceRestClientConfigurer serviceRestClientConfigurer() {
            new ServiceRestClientConfigurerSupport() {
                @Override
                RestOperations getRestTemplate() {
                    return new TestRestTemplate()
                }
            }
        }
    }
}
