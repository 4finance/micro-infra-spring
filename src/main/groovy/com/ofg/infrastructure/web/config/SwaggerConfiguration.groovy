package com.ofg.infrastructure.web.config

import com.mangofactory.swagger.configuration.SpringSwaggerConfig
import com.mangofactory.swagger.plugin.EnableSwagger
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin
import com.wordnik.swagger.model.ApiInfo
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan('com.mangofactory.swagger')
@EnableSwagger
class SwaggerConfiguration {

    @Bean
    SwaggerSpringMvcPlugin swaggerSpringMvcPlugin(
            @Value('${rest.api.version:1.0}') String restApiVersion, SpringSwaggerConfig springSwaggerConfig) {
        return new SwaggerSpringMvcPlugin(springSwaggerConfig)
                .apiInfo(apiInfo())
                .apiVersion(restApiVersion)
                .includePatterns(".*")
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Spring Boot Microservice API",
                "APIs for Spring Boot Microservice template",
                "Terms Of Service - Use On Your Own Risk",
                "My Apps API Contact Email",
                "My Apps API Licence Type",
                "My Apps API License URL"
        )
    }
}
