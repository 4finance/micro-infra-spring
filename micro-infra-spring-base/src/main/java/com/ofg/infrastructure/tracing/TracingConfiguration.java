package com.ofg.infrastructure.tracing;

import com.ofg.infrastructure.scheduling.TaskSchedulingConfiguration;
import org.springframework.cloud.sleuth.IdGenerator;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.Trace;
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({TaskSchedulingConfiguration.class, TraceAutoConfiguration.class, AsyncTracingConfiguration.class})
public class TracingConfiguration {

    /**
     * Our custom trace that wraps the existing trace to catch exceptions
     * TODO: Remove after fixing in Sleuth
     *
     * @param sampler
     * @param idGenerator
     * @param publisher
     * @return
     */
    @Bean
    public Trace trace(Sampler<Void> sampler, IdGenerator idGenerator,
                       ApplicationEventPublisher publisher) {
        return new ExceptionCatchingTrace(sampler, idGenerator, publisher);
    }

}
