package com.ofg.infrastructure.metrics.config

import com.codahale.metrics.graphite.Graphite
import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.metrics.publishing.GraphitePublisher
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.core.env.PropertySource
import org.springframework.core.io.support.ResourcePropertySource
import org.springframework.mock.env.MockPropertySource
import spock.lang.AutoCleanup
import spock.lang.Specification
import spock.lang.Unroll

import static com.ofg.config.BasicProfiles.PRODUCTION
import static com.ofg.infrastructure.metrics.config.IsGraphitePublishingEnabled.GRAPHITE_PUBLISHING

class GraphitePublisherRegistrationSpec extends Specification {

    @AutoCleanup AnnotationConfigApplicationContext applicationContext

    def setup() {
        applicationContext = new AnnotationConfigApplicationContext()
        applicationContext.environment.setActiveProfiles(PRODUCTION)
        applicationContext.register(BaseConfiguration, MetricsConfiguration)
    }

    @Unroll
    def 'should register Graphite beans when Graphite publishing is enabled using #flagValue'() {
        given:
            registerInContext(propertySourceWithGraphitePublishingEnabled(flagValue))
            applicationContext.refresh()
        expect:
            beanIsPresent(GraphitePublisher)
            beanIsPresent(Graphite)
        where:
            flagValue << ['yes', 'on', 'true']
    }

    @Unroll
    def 'should not register Graphite beans when Graphite publishing is disabled using #flagValue'() {
        given:
            registerInContext(propertySourceWithGraphitePublishingDisabled(flagValue))
            applicationContext.refresh()
        expect:
            beanIsAbsent(GraphitePublisher)
            beanIsAbsent(Graphite)
        where:
            flagValue << ['no', 'off', 'false']
    }

    def 'should register Graphite beans when Graphite publishing is not defined'() {
        given:
            applicationContext.refresh()
        expect:
            beanIsPresent(GraphitePublisher)
            beanIsPresent(Graphite)
    }

    def 'should register Graphite beans when Graphite publishing is enabled in application properties file'() {
        given:
            registerInContext(new ResourcePropertySource('graphitePublishingEnabled.properties'))
            applicationContext.refresh()
        expect:
            beanIsPresent(GraphitePublisher)
            beanIsPresent(Graphite)
    }

    def 'should not register Graphite beans when Graphite publishing is disabled in application properties file'() {
        given:
            registerInContext(new ResourcePropertySource('graphitePublishingDisabled.properties'))
            applicationContext.refresh()
        expect:
            beanIsAbsent(GraphitePublisher)
            beanIsAbsent(Graphite)
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

    private beanIsPresent(Class<?> beanClass) {
        applicationContext.getBean(beanClass) != null
    }

    private beanIsAbsent(Class<?> beanClass) {
        applicationContext.getBeanNamesForType(beanClass) == []
    }
}
