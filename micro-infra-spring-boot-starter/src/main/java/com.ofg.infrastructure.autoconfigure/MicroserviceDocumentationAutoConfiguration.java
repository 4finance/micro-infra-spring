package com.ofg.infrastructure.autoconfigure;

import com.ofg.infrastructure.config.EnableMicroserviceDocumentation;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * Auto-configuration} for Swagger API Documentation. Equivalent to enabling
 * {@link com.ofg.infrastructure.config.EnableMicroserviceDocumentation} in your configuration.
 * <p/>
 * The configuration will not be activated if {@literal com.ofg.infra.microservice.documentation.auto=false}.
 *
 * @see com.ofg.infrastructure.config.EnableMicroserviceDocumentation
 */
@Configuration
@ConditionalOnExpression("${com.ofg.infra.microservice.documentation.auto:true}")
@AutoConfigureAfter(org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration.class)
@EnableMicroserviceDocumentation
public class MicroserviceDocumentationAutoConfiguration {
}
