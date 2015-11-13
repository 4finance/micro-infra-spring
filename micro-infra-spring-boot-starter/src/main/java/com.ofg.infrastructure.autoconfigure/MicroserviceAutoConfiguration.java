package com.ofg.infrastructure.autoconfigure;

import com.ofg.config.NotSpringCloudProfile;
import com.ofg.infrastructure.config.EnableMicroservice;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * Auto-configuration} for Microservices. Equivalent to enabling
 * {@link com.ofg.infrastructure.autoconfigure.MicroserviceAutoConfiguration} in your configuration.
 * <p/>
 * The configuration will not be activated if {@literal com.ofg.infrastructure.microservice.auto=false}.
 *
 * @see com.ofg.infrastructure.config.EnableMicroservice
 */
@Configuration
@ConditionalOnExpression("${com.ofg.infra.microservice.auto:true}")
@EnableMicroservice
@NotSpringCloudProfile
public class MicroserviceAutoConfiguration {

}
