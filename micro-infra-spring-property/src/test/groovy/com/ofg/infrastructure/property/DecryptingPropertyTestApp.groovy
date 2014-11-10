package com.ofg.infrastructure.property

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = ['org.springframework.cloud.autoconfigure'])
class DecryptingPropertyTestApp {
}
