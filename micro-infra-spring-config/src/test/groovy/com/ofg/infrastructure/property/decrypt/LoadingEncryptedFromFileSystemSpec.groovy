package com.ofg.infrastructure.property.decrypt

import com.ofg.infrastructure.property.AbstractIntegrationSpec
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.cloud.autoconfigure.ConfigClientAutoConfiguration
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import spock.lang.AutoCleanup
import spock.lang.IgnoreIf
import spock.lang.Shared

import static com.ofg.infrastructure.property.decrypt.JceUnlimitedStrengthTestFixture.shouldDecryptionTestsBeExecuted

@IgnoreIf({ !shouldDecryptionTestsBeExecuted() })
class LoadingEncryptedFromFileSystemSpec extends AbstractIntegrationSpec {

    @Shared
    @AutoCleanup
    private ConfigurableApplicationContext context
    @Shared
    private MyEncBean myEncBean

    def setupSpec() {
        setEncryptKey()
        System.setProperty('microservice.config.file', 'classpath:microservice-enc.json')
        context = new SpringApplicationBuilder(BasicEncApp)
                .web(false)
                .showBanner(false)
                .run()
        myEncBean = context.getBean(MyEncBean)
    }

    def 'should read encrypted property from .properties file'() {
        expect:
            myEncBean.countryPropSecret == 'enc.propertySource.prop'
    }

    def 'should read encrypted property from .yaml file'() {
        expect:
            myEncBean.countryYamlSecret == 'encrypted.yaml.value'
    }
}

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = [ConfigClientAutoConfiguration.class])
class BasicEncApp {

    @Bean
    def myEncBean() {
        return new MyEncBean()
    }
}

class MyEncBean {
    @Value('${country.prop.secret}')
    String countryPropSecret;

    @Value('${country.yaml.secret}')
    String countryYamlSecret;
}
