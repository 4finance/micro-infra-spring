package com.ofg.infrastructure.web.resttemplate.fluent

import com.codahale.metrics.MetricRegistry
import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MvcCorrelationIdSettingIntegrationSpec
import com.ofg.infrastructure.web.resttemplate.MetricsAspect
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = CustomConfig, loader = SpringApplicationContextLoader)
class ServiceRestClientCustomizationByNameSpec extends MvcCorrelationIdSettingIntegrationSpec {

    @Autowired
    private MetricsAspect metricsAspect2

    def "should allow to autowire by name and more than one bean available"() {
        expect:
            metricsAspect2 instanceof TestMetricsAspect2
    }

    @Configuration
    @Import(BaseConfiguration)
    @EnableServiceRestClient
    @CompileStatic
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    static class CustomConfig {
        @Bean
        MetricsAspect metricsAspect2(MetricRegistry metricRegistry) {
            return new TestMetricsAspect2(metricRegistry)
        }

        @Bean
        MetricsAspect metricsAspect3(MetricRegistry metricRegistry) {
            return new TestMetricsAspect3(metricRegistry)
        }
    }

    @InheritConstructors
    static class TestMetricsAspect2 extends MetricsAspect {}

    @InheritConstructors
    static class TestMetricsAspect3 extends MetricsAspect {}
}
