package com.ofg.infrastructure.property

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import spock.lang.Ignore
import spock.lang.Shared

class LoadingFromFileSystemTest extends AbstractIntegrationTest {

    @Shared
    private ConfigurableApplicationContext context
    @Shared
    private MyBean myBean

    def setupSpec() {
        System.setProperty(PropertiesConfiguration.CONFIG_FOLDER, findConfigDirInTestResources())
        System.setProperty(PropertiesConfiguration.COUNTRY_CODE, "pl")
        context = new SpringApplicationBuilder(BasicApp)
                .web(false)
                .showBanner(false)
                .run()
        myBean = context.getBean(MyBean)
    }

    private String findConfigDirInTestResources() {
        URL resourceInSrcTestRoot = getClass().getResource("/logback-test.groovy")
        String srcTestResources = new File(resourceInSrcTestRoot.file).parent
        return new File(srcTestResources, 'test-config-dir').absolutePath
    }

    void cleanupSpec() {
        context?.close()
    }

    def 'should read property from default .properties file'() {
        expect:
            myBean.globalPropKey == 'global prop value'
    }

    def 'should read property from default .yaml file'() {
        expect:
            myBean.countryPropKey == 'country prop value'
    }

    def 'should read property from country-specific .properties file'() {
        expect:
            myBean.globalYamlKey == 'global yaml value'
    }

    def 'should read property from country-specific .yaml file'() {
        expect:
            myBean.countryYamlKey == 'country yaml value'
    }

    def 'should override property from country-specific .properties file'() {
        expect:
            myBean.globalDefaultKey == 'overriden default value'
    }

    def 'should override property from country-specific .yaml file'() {
        expect:
            myBean.globalYamlDefault == 'overriden default yaml value'
    }

    def '.yaml has priority over .properties'() {
        expect:
            myBean.custom == 'yaml value'
            myBean.customCountry == 'yaml country value'
    }

}

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = ['org.springframework.cloud.autoconfigure'])
class BasicApp {

    @Bean
    def myBean() {
        return new MyBean()
    }

}

class MyBean {
    @Value('${global.prop.key}')
    String globalPropKey;

    @Value('${country.prop.key}')
    String countryPropKey;

    @Value('${global.yaml.key}')
    String globalYamlKey;

    @Value('${country.yaml.key}')
    String countryYamlKey;

    @Value('${global.default.key}')
    String globalDefaultKey;

    @Value('${global.yaml.default}')
    String globalYamlDefault;

    @Value('${custom}')
    String custom;

    @Value('${custom.country}')
    String customCountry;
}