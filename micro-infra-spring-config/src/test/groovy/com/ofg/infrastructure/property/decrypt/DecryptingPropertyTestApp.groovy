package com.ofg.infrastructure.property.decrypt

import org.springframework.cloud.config.client.ConfigClientAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackageClasses = [ConfigClientAutoConfiguration.class])
class DecryptingPropertyTestApp {
}
