package com.ofg.infrastructure.web.view

import com.fasterxml.jackson.core.JsonGenerator
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
class DisablingJsonGeneratorFeaturesSpec extends Specification {

    @Autowired WebApplicationContext webApplicationContext

    @Unroll
    def "should enable JsonGenerator's feature #generatorFeature"() {
        given:
            def converter = getPredefinedJacksonMessageConverter()
            def feature = JsonGenerator.Feature.valueOf(generatorFeature)
        expect:
            converter.objectMapper.isEnabled(feature) == false
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
