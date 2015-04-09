package com.ofg.infrastructure.property.decrypt

import com.ofg.infrastructure.property.AbstractIntegrationSpec
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component
import spock.lang.AutoCleanup
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Issue
import spock.lang.Shared

import static com.ofg.infrastructure.property.decrypt.JceUnlimitedStrengthUtil.isStrongEncryptionSupported

@IgnoreIf({ !isStrongEncryptionSupported() })
class DecryptingPropertySpec extends AbstractIntegrationSpec {

    @Shared
    @AutoCleanup
    private ConfigurableApplicationContext context

    def setupSpec() {
        setEncryptKey()
        context = applicationBuilderWithSources(
                DecryptingPropertyTestApp, TestConfigurationWithPropertySource, ConfigurationPropertiesSettings)
                .properties("enc.prop:{cipher}f43b8323cd82a74aafa1fba5efdce529274b58f68145903e6cc7e460e07e0e20")
                .run("--spring.config.name=decryptingPropertyTest")
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
    @Issue("https://github.com/spring-cloud/spring-cloud-config/issues/30")
    def "should decrypt properties added with @PropertySource"() {
        expect:
            context.environment.getProperty("enc.propertySource.prop") == "enc.propertySource.prop.value"
    }

    @Ignore("Not supported")
    def "should decrypt properties added with @ConfigurationProperties"() {
        expect:
            context.getBean(ConfigurationPropertiesSettings)?.configurationProperties == "enc.propertySource.prop.value"
    }
}

@Configuration
@PropertySource("classpath:testConfigurationWithPropertySource.properties")
@EnableConfigurationProperties
class TestConfigurationWithPropertySource {
}

@Component
@ConfigurationProperties(locations = "classpath:testConfigurationWithConfigurationProperties.properties", prefix = "enc")
class ConfigurationPropertiesSettings {
    String configurationProperties
}
