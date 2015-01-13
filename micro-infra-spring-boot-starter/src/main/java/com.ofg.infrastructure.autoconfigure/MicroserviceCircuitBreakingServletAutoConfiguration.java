package com.ofg.infrastructure.autoconfigure;

import com.ofg.infrastructure.hystrix.EnableHystrixServlet;
import org.springframework.context.annotation.Configuration;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * Auto-configuration} for Microservices. Equivalent to enabling
 * {@link com.ofg.infrastructure.hystrix.HystrixServletConfiguration} in your configuration.
 * <p/>
 * Check {@link com.ofg.infrastructure.hystrix.IsHystrixServletEnabled} to see what conditions
 * need to be satisfied to register the servlet.
 *
 * @see EnableHystrixServlet
 * @see com.ofg.infrastructure.hystrix.IsHystrixServletEnabled
 */
@Configuration
@EnableHystrixServlet
public class MicroserviceCircuitBreakingServletAutoConfiguration {

}
