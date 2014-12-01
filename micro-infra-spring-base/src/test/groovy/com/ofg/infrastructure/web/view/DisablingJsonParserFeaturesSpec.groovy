package com.ofg.infrastructure.web.view

import com.fasterxml.jackson.core.JsonParser
import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.ConfigurationWithoutServiceDiscovery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.MutablePropertySources
import org.springframework.core.env.StandardEnvironment
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.mock.env.MockPropertySource
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import spock.lang.Specification
import spock.lang.Unroll

import static com.ofg.config.BasicProfiles.TEST

@ContextConfiguration(classes = [Config, BaseConfiguration, ConfigurationWithoutServiceDiscovery],
                      initializers = PropertyMockingApplicationContextInitializer.class)
@WebAppConfiguration
@ActiveProfiles(TEST)
class DisablingJsonParserFeaturesSpec extends Specification {

    @Autowired WebApplicationContext webApplicationContext

    @Unroll
    def "should disable JsonParser's feature #parserFeature"() {
        given:
            def converter = getPredefinedJacksonMessageConverter()
            def feature = JsonParser.Feature.valueOf(parserFeature)
        expect:
            converter.objectMapper.isEnabled(feature) == false
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

    private MappingJackson2HttpMessageConverter getPredefinedJacksonMessageConverter() {
        def mappingHandlerAdapter = webApplicationContext.getBean(RequestMappingHandlerAdapter.class)
        def jacksonMessageConverter = mappingHandlerAdapter.getMessageConverters().find { it -> it instanceof MappingJackson2HttpMessageConverter }
        if (jacksonMessageConverter) {
            return jacksonMessageConverter
        } else {
            throw new IllegalStateException("Unable to find predefined instance of MappingJackson2HttpMessageConverter class.")
        }
    }

    @Configuration
    static class Config {
        @Bean
        TestController testController() {
            return new TestController()
        }
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
