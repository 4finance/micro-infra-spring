package com.ofg.infrastructure.property

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.cloud.config.client.ConfigClientAutoConfiguration
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import spock.lang.AutoCleanup
import spock.lang.Shared

class LoadingFromFileSystemSpec extends AbstractIntegrationSpec {

    @Shared
    @AutoCleanup
    private ConfigurableApplicationContext context
    @Shared
    private MyBean myBean

    def setupSpec() {
        setValidBootstrapConfig()
        context = contextWithSources(BasicApp)
        myBean = context.getBean(MyBean)
    }

    def 'should read property from global .properties file'() {
        expect:
            myBean.globalPropKey == 'global prop value'
    }

    def 'should read property from common .properties file'() {
        expect:
            myBean.commonPropKey == 'common prop value'
    }

    def 'should read property from env .properties file'() {
        expect:
            myBean.envPropKey == 'env prop value'
    }

    def 'should read property from common country-specific .properties file'() {
        expect:
            myBean.commonCountryPropKey== 'common country prop value'
    }

    def 'should read property from country-specific .properties file'() {
        expect:
            myBean.countryPropKey == 'country prop value'
    }

    def 'should read property from global .yaml file'() {
        expect:
            myBean.globalYamlKey == 'global yaml value'
    }

    def 'should read property from common .yaml file'() {
        expect:
            myBean.commonYamlKey == 'common yaml value'
    }

    def 'should read property from env .yaml file'() {
        expect:
            myBean.envYamlKey == 'env yaml value'
    }

    def 'should read property from common country-specific .yaml file'() {
        expect:
            myBean.commonCountryYamlKey == 'common country yaml value'
    }

    def 'should read property from country-specific .yaml file'() {
        expect:
            myBean.countryYamlKey == 'country yaml value'
    }

    def 'should override property from country-specific .properties file'() {
        expect:
            myBean.globalDefaultPropKey == 'overridden country-specific prop value'
    }

    def 'should override property from country-specific .yaml file'() {
        expect:
            myBean.globalDefaultYamlKey == 'overridden country-specific yaml value'
    }

    def 'should read unquoted URLs'() {
        expect:
            myBean.globalUrl == 'http://example.com'
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

    @Value('${global.prop.key}')
    String globalPropKey

    @Value('${common.prop.key}')
    String commonPropKey

    @Value('${env.prop.key}')
    String envPropKey

    @Value('${common.country.prop.key}')
    String commonCountryPropKey

    @Value('${country.prop.key}')
    String countryPropKey

    @Value('${global.yaml.key}')
    String globalYamlKey

    @Value('${common.yaml.key}')
    String commonYamlKey

    @Value('${env.yaml.key}')
    String envYamlKey

    @Value('${common.country.yaml.key}')
    String commonCountryYamlKey

    @Value('${country.yaml.key}')
    String countryYamlKey

    @Value('${global.default.prop.key}')
    String globalDefaultPropKey

    @Value('${global.default.yaml.key}')
    String globalDefaultYamlKey

    @Value('${custom.global.key}')
    String customGlobalKey

    @Value('${custom.common.key}')
    String customCommonKey

    @Value('${custom.env.key}')
    String customEnvKey

    @Value('${custom.common.country.key}')
    String customCommonCountryKey

    @Value('${custom.country.key}')
    String customCountryKey

    @Value('${global.url}')
    String globalUrl
}
