package com.ofg.infrastructure.property

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.cloud.autoconfigure.ConfigClientAutoConfiguration
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import spock.lang.AutoCleanup
import spock.lang.Shared

class LoadingFromFileSystemTest extends AbstractIntegrationTest {

    @Shared
    @AutoCleanup
    private ConfigurableApplicationContext context
    @Shared
    private MyBean myBean

    def setupSpec() {
        System.setProperty("encrypt.key", "eKey")
        context = new SpringApplicationBuilder(BasicApp)
                .web(false)
                .showBanner(false)
                .run()
        myBean = context.getBean(MyBean)
    }

    def 'should read property from common .properties file'() {
        expect:
            myBean.commonPropKey == 'common prop value'
    }

    def 'should read property from env .properties file'() {
        expect:
            myBean.envPropKey == 'env prop value'
    }

    def 'should read property from country-specific .properties file'() {
        expect:
            myBean.countryPropKey == 'country prop value'
    }

    def 'should read property from common .yaml file'() {
        expect:
            myBean.commonYamlKey == 'common yaml value'
    }

    def 'should read property from env .yaml file'() {
        expect:
            myBean.envYamlKey == 'env yaml value'
    }

    def 'should read property from country-specific .yaml file'() {
        expect:
            myBean.countryYamlKey == 'country yaml value'
    }

    def 'should override property from country-specific .properties file'() {
        expect:
            myBean.commonDefaultPropKey == 'overridden country-specific prop value'
    }

    def 'should override property from country-specific .yaml file'() {
        expect:
            myBean.commonDefaultYamlKey == 'overridden country-specific yaml value'
    }

    def '.yaml has priority over .properties'() {
        expect:
            myBean.customCommonKey == 'custom common yaml value'
            myBean.customEnvKey == 'custom env yaml value'
            myBean.customCountryKey == 'custom country yaml value'
    }
}

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = [ConfigClientAutoConfiguration.class])
class BasicApp {

    @Bean
    def myBean() {
        return new MyBean()
    }
}

class MyBean {

    @Value('${common.prop.key}')
    String commonPropKey

    @Value('${env.prop.key}')
    String envPropKey

    @Value('${country.prop.key}')
    String countryPropKey

    @Value('${common.yaml.key}')
    String commonYamlKey

    @Value('${env.yaml.key}')
    String envYamlKey

    @Value('${country.yaml.key}')
    String countryYamlKey

    @Value('${common.default.prop.key}')
    String commonDefaultPropKey

    @Value('${common.default.yaml.key}')
    String commonDefaultYamlKey

    @Value('${custom.common.key}')
    String customCommonKey

    @Value('${custom.env.key}')
    String customEnvKey

    @Value('${custom.country.key}')
    String customCountryKey

}
