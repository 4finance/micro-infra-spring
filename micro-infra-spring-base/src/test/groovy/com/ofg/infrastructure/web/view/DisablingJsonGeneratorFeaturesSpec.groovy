package com.ofg.infrastructure.web.view

import com.fasterxml.jackson.core.JsonGenerator
import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.ConfigurationWithoutServiceDiscovery
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MutablePropertySources
import org.springframework.core.env.StandardEnvironment
import org.springframework.mock.env.MockPropertySource
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll

@ContextConfiguration(classes = [Config, BaseConfiguration, ConfigurationWithoutServiceDiscovery],
                      initializers = PropertyMockingApplicationContextInitializer.class)
class DisablingJsonGeneratorFeaturesSpec extends JsonJacksonFeaturesSpec {

    @Unroll
    def "should enable JsonGenerator's feature #generatorFeature"() {
        given:
            def converter = getPredefinedJacksonMessageConverter()
            def feature = JsonGenerator.Feature.valueOf(generatorFeature)
        expect:
            !converter.objectMapper.isEnabled(feature)
        where:
            generatorFeature << ['AUTO_CLOSE_JSON_CONTENT',
                              'AUTO_CLOSE_TARGET',
                              'ESCAPE_NON_ASCII',
                              'FLUSH_PASSED_TO_STREAM',
                              'QUOTE_FIELD_NAMES',
                              'QUOTE_NON_NUMERIC_NUMBERS',
                              'STRICT_DUPLICATE_DETECTION',
                              'WRITE_BIGDECIMAL_AS_PLAIN',
                              'WRITE_NUMBERS_AS_STRINGS']
    }

    static class PropertyMockingApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources()
            MockPropertySource mockEnvVars = new MockPropertySource().withProperty('json.jackson.generator.off',
                                            """AUTO_CLOSE_JSON_CONTENT,
                                                AUTO_CLOSE_TARGET,
                                                ESCAPE_NON_ASCII,
                                                FLUSH_PASSED_TO_STREAM,
                                                QUOTE_FIELD_NAMES,
                                                QUOTE_NON_NUMERIC_NUMBERS,
                                                STRICT_DUPLICATE_DETECTION,
                                                WRITE_BIGDECIMAL_AS_PLAIN,
                                                WRITE_NUMBERS_AS_STRINGS""")
            propertySources.replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, mockEnvVars)
        }
    }
}
