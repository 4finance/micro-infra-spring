package com.ofg.infrastructure.web.view

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.converter.ByteArrayHttpMessageConverter
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.ResourceHttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
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
@TypeChecked
@Configuration
class ViewConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    Environment environment

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);

        converters.add(new ByteArrayHttpMessageConverter())
        converters.add(new StringHttpMessageConverter())
        converters.add(new ResourceHttpMessageConverter())
        converters.add(new SourceHttpMessageConverter())
        converters.add(new FormHttpMessageConverter())
        converters.add(mappingJackson2HttpMessageConverter());
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter()
        converter.prettyPrint = prettyPrintingBasedOnProfile()
        converter.objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        return converter
    }

    private boolean prettyPrintingBasedOnProfile() {
        return environment.acceptsProfiles(DEVELOPMENT, TEST)
    }
}
