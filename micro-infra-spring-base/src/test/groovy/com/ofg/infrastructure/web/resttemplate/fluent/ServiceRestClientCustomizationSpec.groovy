package com.ofg.infrastructure.web.resttemplate.fluent

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MvcCorrelationIdSettingIntegrationSpec
import com.ofg.infrastructure.discovery.EnableServiceDiscovery
import com.ofg.infrastructure.web.resttemplate.custom.RestTemplate
import com.ofg.infrastructure.web.resttemplate.fluent.config.ServiceRestClientConfigurerAdapter
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = CustomConfig, loader = SpringApplicationContextLoader)
class ServiceRestClientCustomizationSpec extends MvcCorrelationIdSettingIntegrationSpec {

    static
    final MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter()

    @Autowired
    ApplicationContext applicationContext

    def "should allow to provide custom MessageConverter via ServiceRestClientConfigurer"() {
        given:
            RestTemplate restTemplate = applicationContext.getBean(RestTemplate)
        expect:
            restTemplate.getMessageConverters().contains(jackson2HttpMessageConverter)
    }

    @Configuration
    @Import(BaseConfiguration)
    @EnableServiceRestClient
    @EnableServiceDiscovery
    @CompileStatic
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    static class CustomConfig extends ServiceRestClientConfigurerAdapter {
        @Override
        void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(jackson2HttpMessageConverter)
        }
    }
}
