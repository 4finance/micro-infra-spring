package com.ofg.infrastructure.metrics.config

import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GraphiteServiceConfig {

    @Bean ServiceConfigurationResolver serviceConfigurationResolver() {
        return new ServiceConfigurationResolver("""
                            {
                                "realm": {
                                    "this": "io/fourfinanceit/some-super-name"
                                }
                            }
                            """)
    }
}
