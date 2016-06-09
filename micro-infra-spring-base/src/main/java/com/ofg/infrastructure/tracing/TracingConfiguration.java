package com.ofg.infrastructure.tracing;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class TracingConfiguration {

    @Bean
    public Sampler alwaysSampler() {
        return new AlwaysSampler();
    }
}
