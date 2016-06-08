package com.ofg.infrastructure.tracing;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration;
import org.springframework.cloud.sleuth.instrument.web.TraceWebAutoConfiguration;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore({TraceAutoConfiguration.class, TraceWebAutoConfiguration.class})
public class TracingConfiguration {

    @Bean
    public Sampler alwaysSampler() {
        return new AlwaysSampler();
    }
}
