package com.ofg.infrastructure.property

import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

class DecryptingPropertyTest extends Specification {

    @Shared
    private ConfigurableApplicationContext context

    void setupSpec() {
        System.setProperty("encrypt.key", "eKey")   //To simulate setting environment variable
        context = new SpringApplicationBuilder(DecryptingPropertyTestApp, TestConfigurationWithPropertySource)
                .web(false)
                .showBanner(false)
                .properties("enc.prop:{cipher}f43b8323cd82a74aafa1fba5efdce529274b58f68145903e6cc7e460e07e0e20")
                .run("--spring.config.name=decryptingPropertyTest")
    }

    void afterSpec() {
        System.properties.remove("encrypt.key") //TODO: Replace with @RestoreSystemProperties from Spock 1.0, when available...
        context?.close()
    }

    def "should decrypt properties from application.properties"() {
        expect:
            context.environment.getProperty("enc.application.prop") == "enc.application.prop.value"
    }

    def "should decrypt properties from set properties"() {
        expect:
            context.environment.getProperty("enc.prop") == "enc.prop.value"
    }

    @Ignore("Currently EnvironmentDecryptApplicationListener is run too early")
    def "should decrypt properties added with @PropertySource"() {
        expect:
            context.environment.getProperty("enc.propertySource.prop") == "enc.propertySource.prop.value"
    }
}

@Configuration
@PropertySource("testConfigurationWithPropertySource.properties")
class TestConfigurationWithPropertySource {
}
