package com.ofg.infrastructure.metrics.config

import com.codahale.metrics.graphite.GraphiteSender
import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.metrics.publishing.EnvironmentAwareMetricsBasePath
import com.ofg.infrastructure.metrics.publishing.GraphitePublisher
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.core.env.PropertySource
import org.springframework.core.io.support.ResourcePropertySource
import org.springframework.mock.env.MockPropertySource
import spock.lang.AutoCleanup
import spock.lang.Specification
import spock.lang.Unroll

import java.util.regex.Pattern

import static com.ofg.config.BasicProfiles.PRODUCTION
import static com.ofg.infrastructure.metrics.config.IsGraphitePublishingEnabled.GRAPHITE_PUBLISHING

class GraphitePublisherRegistrationSpec extends Specification {

    @AutoCleanup AnnotationConfigApplicationContext applicationContext

    def setup() {
        applicationContext = new AnnotationConfigApplicationContext()
        applicationContext.environment.setActiveProfiles(PRODUCTION)
        applicationContext.register(BaseConfiguration, GraphiteServiceConfig, MetricsConfiguration)
    }

    def 'should register Graphite beans when Graphite publishing is enabled using #flagValue'() {
        given:
            registerInContext(propertySourceWithGraphitePublishingEnabled(flagValue))
            applicationContext.refresh()
        expect:
            beanIsPresent(GraphitePublisher)
            beanIsPresent(GraphiteSender)
        where:
            flagValue << ['yes', 'on', 'true']
    }

    def 'should not register Graphite beans when Graphite publishing is disabled using #flagValue'() {
        given:
            registerInContext(propertySourceWithGraphitePublishingDisabled(flagValue))
            applicationContext.refresh()
        expect:
            beanIsAbsent(GraphitePublisher)
            beanIsAbsent(GraphiteSender)
        where:
            flagValue << ['no', 'off', 'false']
    }

    def 'should register Graphite beans when Graphite publishing is not defined'() {
        given:
            applicationContext.refresh()
        expect:
            beanIsPresent(GraphitePublisher)
            beanIsPresent(GraphiteSender)
    }

    def 'should register Graphite beans when Graphite publishing is enabled in application properties file'() {
        given:
            registerInContext(new ResourcePropertySource('graphitePublishingEnabled.properties'))
            applicationContext.refresh()
        expect:
            beanIsPresent(GraphitePublisher)
            beanIsPresent(GraphiteSender)
    }

    def 'should not register Graphite beans when Graphite publishing is disabled in application properties file'() {
        given:
            registerInContext(new ResourcePropertySource('graphitePublishingDisabled.properties'))
            applicationContext.refresh()
        expect:
            beanIsAbsent(GraphitePublisher)
            beanIsAbsent(GraphiteSender)
    }

    def 'should pick values from microservice.json as defaults for metrics if props are not passed explicitly'() {
        given:
            applicationContext.refresh()
        expect:
            metricPathConsistsOf(microserviceJsonEntries())
    }

    def 'should ignore microservice.json values and the ones from props if they have been passed explicitly'() {
        given:
            registerInContext(new ResourcePropertySource('graphiteWithDefaultProps.properties'))
            applicationContext.refresh()
        expect:
            metricPathConsistsOf(valuesFromPropertyFile())
    }

    private registerInContext(PropertySource propertySource) {
        applicationContext.environment.propertySources.addFirst(propertySource)
    }

    private propertySourceWithGraphitePublishingEnabled(String flagValue) {
        return new MockPropertySource().withProperty(GRAPHITE_PUBLISHING, flagValue)
    }

    private propertySourceWithGraphitePublishingDisabled(String flagValue) {
        return new MockPropertySource().withProperty(GRAPHITE_PUBLISHING, flagValue)
    }

    private boolean beanIsPresent(Class<?> beanClass) {
        applicationContext.getBean(beanClass) != null
    }

    private boolean beanIsAbsent(Class<?> beanClass) {
        applicationContext.getBeanNamesForType(beanClass) == []
    }

    private boolean metricPathConsistsOf(Pattern regex) {
        applicationContext.getBean(EnvironmentAwareMetricsBasePath).path.matches(regex)
    }

    private Pattern microserviceJsonEntries() {
        return ~/^.*realm.some-super-name.*$/
    }

    private Pattern valuesFromPropertyFile() {
        return ~/^.*country-not-from-json.imaginary-app.*$/
    }
}
