package com.ofg.infrastructure.web.view

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.ConfigurationWithoutServiceDiscovery
import com.ofg.infrastructure.base.MvcCorrelationIdSettingIntegrationSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.MutablePropertySources
import org.springframework.core.env.StandardEnvironment
import org.springframework.http.MediaType
import org.springframework.mock.env.MockPropertySource
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

import static com.ofg.config.BasicProfiles.DEVELOPMENT
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = [Config, BaseConfiguration, ConfigurationWithoutServiceDiscovery, ViewConfiguration],
                      initializers = PropertyMockingApplicationContextInitializer.class)
@TestPropertySource(properties = ["spring.cloud.service-registry.auto-registration.enabled=false"])
@ActiveProfiles(DEVELOPMENT)
class AcceptingUnquotedFieldsInJsonSpec extends MvcCorrelationIdSettingIntegrationSpec {

    String REQUEST_JSON_BODY = "{sampleField:\"sampleValue\"}"

    def "should accept JSON with unquoted field names"() {
        expect:
            mockMvc.perform(post("/test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(REQUEST_JSON_BODY))
                .andExpect(status().isCreated())
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
            MockPropertySource mockEnvVars = new MockPropertySource().withProperty('json.jackson.parser.on', 'ALLOW_UNQUOTED_FIELD_NAMES')
            propertySources.replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, mockEnvVars)
        }
    }
}
