package com.ofg.infrastructure.base

import com.ofg.infrastructure.tracing.EnableTracing
import groovy.transform.CompileStatic
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration
import org.springframework.cloud.sleuth.zipkin.ZipkinAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

@CompileStatic
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Configuration
@EnableTracing
@EnableAutoConfiguration(exclude = [LoadBalancerAutoConfiguration, ZipkinAutoConfiguration, JmxAutoConfiguration])
class BaseConfiguration {

    @Bean
    static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer()
    }

}
