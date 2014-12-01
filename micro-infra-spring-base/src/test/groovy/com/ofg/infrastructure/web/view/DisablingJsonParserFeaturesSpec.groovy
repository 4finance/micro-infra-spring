package com.ofg.infrastructure.web.view

import com.fasterxml.jackson.core.JsonParser
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
class DisablingJsonParserFeaturesSpec extends JsonJacksonFeaturesSpec {

    @Unroll
    def "should disable JsonParser's feature #parserFeature"() {
        given:
            def converter = getPredefinedJacksonMessageConverter()
            def feature = JsonParser.Feature.valueOf(parserFeature)
        expect:
            !converter.objectMapper.isEnabled(feature)
        where:
            parserFeature << ['AUTO_CLOSE_SOURCE',
                              'ALLOW_UNQUOTED_FIELD_NAMES',
                              'ALLOW_COMMENTS',
                              'ALLOW_YAML_COMMENTS',
                              'ALLOW_SINGLE_QUOTES',
                              'ALLOW_UNQUOTED_CONTROL_CHARS',
                              'ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER',
                              'ALLOW_NUMERIC_LEADING_ZEROS',
                              'ALLOW_NON_NUMERIC_NUMBERS',
                              'STRICT_DUPLICATE_DETECTION']
    }

    static class PropertyMockingApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources()
            MockPropertySource mockEnvVars = new MockPropertySource().withProperty('json.jackson.parser.off',
                                            """AUTO_CLOSE_SOURCE,
                                                ALLOW_UNQUOTED_FIELD_NAMES,
                                                ALLOW_COMMENTS,
                                                ALLOW_YAML_COMMENTS,
                                                ALLOW_SINGLE_QUOTES,
                                                ALLOW_UNQUOTED_CONTROL_CHARS,
                                                ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER,
                                                ALLOW_NUMERIC_LEADING_ZEROS,
                                                ALLOW_NON_NUMERIC_NUMBERS,
                                                STRICT_DUPLICATE_DETECTION""")
            propertySources.replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, mockEnvVars)
        }
    }
}
