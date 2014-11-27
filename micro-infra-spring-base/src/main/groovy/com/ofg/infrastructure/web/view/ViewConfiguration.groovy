package com.ofg.infrastructure.web.view

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonParser
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.converter.*
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.http.converter.xml.SourceHttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

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
class ViewConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    Environment environment

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters)
        converters.addAll([new ByteArrayHttpMessageConverter(),
                           new StringHttpMessageConverter(),
                           new ResourceHttpMessageConverter(),
                           new SourceHttpMessageConverter(),
                           new FormHttpMessageConverter(),
                           mappingJackson2HttpMessageConverter()])
    }

    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter()
        converter.prettyPrint = prettyPrintingBasedOnProfile()
        converter.objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        configureJacksonJsonParser(converter)
        return converter
    }

    private void configureJacksonJsonParser(MappingJackson2HttpMessageConverter converter) {
        String jsonJacksonParserFeatures = environment.getProperty('json.jackson.parser', String.class, '').trim()
        if (jsonJacksonParserFeatures) {
            doConfigureParserFeatures(jsonJacksonParserFeatures, converter)
        }
    }

    private void doConfigureParserFeatures(String jsonJacksonParserFeatures, MappingJackson2HttpMessageConverter converter) {
        jsonJacksonParserFeatures.split(',').each {
            converter.objectMapper.configure(JsonParser.Feature.valueOf(it as String), true)
        }
    }

    private boolean prettyPrintingBasedOnProfile() {
        return environment.acceptsProfiles(DEVELOPMENT, TEST)
    }
}
