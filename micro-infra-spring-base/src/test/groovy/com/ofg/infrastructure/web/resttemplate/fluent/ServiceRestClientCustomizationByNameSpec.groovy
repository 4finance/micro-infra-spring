package com.ofg.infrastructure.web.resttemplate.fluent

import com.codahale.metrics.MetricRegistry
import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MvcCorrelationIdSettingIntegrationSpec
import com.ofg.infrastructure.discovery.EnableServiceDiscovery
import com.ofg.infrastructure.web.resttemplate.RestOperationsMetricsAspect
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
    private RestOperationsMetricsAspect metricsAspect2

    def "should allow to autowire by name and more than one bean available"() {
        expect:
            metricsAspect2 instanceof TestRestOperationsMetricsAspect2
    }

    @Configuration
    @Import(BaseConfiguration)
    @EnableServiceRestClient
    @EnableServiceDiscovery
    @CompileStatic
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    static class CustomConfig {
        @Bean
        RestOperationsMetricsAspect metricsAspect2(MetricRegistry metricRegistry, URIMetricNamer uriMetricNamer) {
            return new TestRestOperationsMetricsAspect2(metricRegistry, uriMetricNamer)
        }

        @Bean
        RestOperationsMetricsAspect metricsAspect3(MetricRegistry metricRegistry, URIMetricNamer uriMetricNamer) {
            return new TestRestOperationsMetricsAspect3(metricRegistry, uriMetricNamer)
        }
    }

    @InheritConstructors
    static class TestRestOperationsMetricsAspect2 extends RestOperationsMetricsAspect {}

    @InheritConstructors
    static class TestRestOperationsMetricsAspect3 extends RestOperationsMetricsAspect {}
}
