package com.ofg.infrastructure.property

import org.springframework.cloud.bootstrap.BootstrapApplicationListener
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MutablePropertySources
import org.springframework.core.env.PropertySource
import org.springframework.core.env.StandardEnvironment
import spock.lang.AutoCleanup
import spock.lang.Shared

class BootstrapPropertySourceOverrideSpec extends AbstractIntegrationSpec {

    @AutoCleanup
    private ConfigurableApplicationContext context

    private MutablePropertySources propertySources

    def setup() {
        context = contextWithSources(BootstrapPropertySourceOverrideSpec)
        propertySources = context.environment.propertySources
    }

    def 'bootstrap property source should be after system environment property source'() {
        when:
            int systemEnvironmentPrecedence = getPropertySourcePrecedence(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME)
            int bootstrapPrecedence = getPropertySourcePrecedence(BootstrapApplicationListener.BOOTSTRAP_PROPERTY_SOURCE_NAME)
        then:
            systemEnvironmentPrecedence < bootstrapPrecedence
    }

    private int getPropertySourcePrecedence(String propertySourceName) {
        def propertySource = propertySources.get(propertySourceName)
        return propertySources.precedenceOf(propertySource);
    }
}
