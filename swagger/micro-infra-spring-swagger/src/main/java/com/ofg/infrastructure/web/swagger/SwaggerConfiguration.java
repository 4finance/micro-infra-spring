package com.ofg.infrastructure.web.swagger;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Adds configuration enabling Swagger in Spring via {@link Docket}
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    public Docket swaggerSpringMvcPlugin(ApiInfo apiInfo,
                                         @Value("${rest.api.urls.to.list:.*}") String urlsToList) {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .select()
                .paths(or(regex(urlsToList)))
                .build();
    }

    @Bean
    public ApiInfo apiInfo(@Value("${rest.api.title:Microservice API}") String title,
                           @Value("${rest.api.description:APIs for this microservice}") String description,
                           @Value("${rest.api.version:1.0}") String apiVersion,
                           @Value("${rest.api.terms:Defined by 4finance internal licences}") String terms,
                           @Value("${rest.api.contact:info@4finance.com}") String contactEmail,
                           @Value("${rest.api.license.type:4finance internal licence}") String licenseType,
                           @Value("${rest.api.license.url:http://4finance.com}") String licenseUrl) {
        return new ApiInfo(title, description, apiVersion, terms, new Contact(null, null, contactEmail), licenseType, licenseUrl);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger/**", "/*.js", "/images/**", "/lib/**", "/css/**")
                .addResourceLocations("classpath:/webjars/springfox-swagger-ui/", "classpath:/webjars/springfox-swagger-ui/images/",
                        "classpath:/webjars/springfox-swagger-ui/lib/", "classpath:/webjars/springfox-swagger-ui/css/");
    }
}
