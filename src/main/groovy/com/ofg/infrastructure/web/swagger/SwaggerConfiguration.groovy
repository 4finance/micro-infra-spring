package com.ofg.infrastructure.web.swagger

import com.mangofactory.swagger.configuration.SpringSwaggerConfig
import com.mangofactory.swagger.plugin.EnableSwagger
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin
import com.wordnik.swagger.model.ApiInfo
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * Adds configuration enabling Swagger in Spring via {@link SwaggerSpringMvcPlugin}
 */
@Configuration
@ComponentScan('com.mangofactory.swagger')
@EnableSwagger
class SwaggerConfiguration {

    @Bean
    SwaggerSpringMvcPlugin swaggerSpringMvcPlugin(
            @Value('${rest.api.version:1.0}') String restApiVersion, 
            SpringSwaggerConfig springSwaggerConfig,
            @Value('${rest.api.title:Microservice API}') String restApiTitle,
            @Value('${rest.api.description:APIs for this microservice}') String restApiDescription,
            @Value('${rest.api.terms:Defined by 4finance internal licences}') String restApiTerms,
            @Value('${rest.api.contact:info@4finance.com}') String restApiContact,
            @Value('${rest.api.license.type:4finance internal licence}') String restApiLicenseType,
            @Value('${rest.api.license.url:http://4finance.com}') String restApiLicenseUrl) {
        return new SwaggerSpringMvcPlugin(springSwaggerConfig)
                .apiInfo(new ApiInfo(
                                    restApiTitle,
                                    restApiDescription,
                                    restApiTerms,
                                    restApiContact,
                                    restApiLicenseType,
                                    restApiLicenseUrl))
                .apiVersion(restApiVersion)
                .includePatterns(".*")
    }

}
