package com.ofg.infrastructure.hystrix

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.embedded.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration

/**
 * Registers a servlet that will be streaming Hystrix data
 *
 * @see HystrixMetricsStreamServlet
 */
@CompileStatic
@Configuration
@Conditional(IsHystrixServletEnabled.class)
class HystrixServletConfiguration {

    @Bean
    public ServletRegistrationBean servletRegistrationBean(
            @Value('${hystrix.stream-servlet.path:/health/hystrix.stream}') String hystrixMetricsStreamPath) {
        return new ServletRegistrationBean(new HystrixMetricsStreamServlet(), hystrixMetricsStreamPath)
    }

}
