package com.ofg.infrastructure.web.view

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.converter.*
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport

import static com.ofg.config.BasicProfiles.DEVELOPMENT
import static com.ofg.config.BasicProfiles.TEST

/**
 * Configures JSON serialization for objects returned by controllers' methods.
 * Pretty printing setting is based on active profile:
 * - in production environment pretty printing is set to false,
 * - in test or development environment pretty printing is set to true.
 */
@CompileStatic
@Configuration
class ViewConfiguration extends WebMvcConfigurationSupport {

    private static final boolean ON = true
    private static final boolean OFF = false

    @Autowired
    private Environment environment

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(mappingJackson2HttpMessageConverter())
        super.addDefaultHttpMessageConverters(converters)
    }

    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter()
        converter.prettyPrint = prettyPrintingBasedOnProfile()
        converter.objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        configureJsonJacksonFeatures(converter)
        return converter
    }

    private boolean prettyPrintingBasedOnProfile() {
        return environment.acceptsProfiles(DEVELOPMENT, TEST)
    }

    private void configureJsonJacksonFeatures(MappingJackson2HttpMessageConverter converter) {
        configureParserFeatures('json.jackson.parser.on', converter, ON)
        configureParserFeatures('json.jackson.parser.off', converter, OFF)
        configureGeneratorFeatures('json.jackson.generator.on', converter, ON)
        configureGeneratorFeatures('json.jackson.generator.off', converter, OFF)
    }

    private void configureParserFeatures(String jsonFeaturesConfigProperty, MappingJackson2HttpMessageConverter converter, boolean featuresState) {
        String jsonJacksonFeatures = environment.getProperty(jsonFeaturesConfigProperty, String.class, '').trim()
        if (jsonJacksonFeatures) {
            jsonJacksonFeatures.split(',').each { it ->
                String featureName = (it as String).trim()
                converter.objectMapper.configure(JsonParser.Feature.valueOf(featureName), featuresState)
            }
        }
    }

    private void configureGeneratorFeatures(String jsonFeaturesConfigProperty, MappingJackson2HttpMessageConverter converter, boolean featuresState) {
        String jsonJacksonFeatures = environment.getProperty(jsonFeaturesConfigProperty, String.class, '').trim()
        if (jsonJacksonFeatures) {
            jsonJacksonFeatures.split(',').each { it ->
                String featureName = (it as String).trim()
                converter.objectMapper.configure(JsonGenerator.Feature.valueOf(featureName), featuresState)
            }
        }
    }
}
