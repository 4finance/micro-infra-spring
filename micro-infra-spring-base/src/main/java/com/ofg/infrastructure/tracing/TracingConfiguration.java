package com.ofg.infrastructure.tracing;

import com.ofg.infrastructure.scheduling.TaskSchedulingConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.*;
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration;
import org.springframework.cloud.sleuth.event.SpanReleasedEvent;
import org.springframework.cloud.sleuth.trace.DefaultTrace;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.invoke.MethodHandles;

@Configuration
@Import({TaskSchedulingConfiguration.class, TraceAutoConfiguration.class, AsyncTracingConfiguration.class})
public class TracingConfiguration {

    @Bean
    public Trace trace(Sampler<Void> sampler, IdGenerator idGenerator,
                       ApplicationEventPublisher publisher) {
        return new DefaultTrace(sampler, idGenerator, publisher) {
            @Override
            public TraceScope continueSpan(Span span) {
                return new ExceptionCatchingTraceScope(super.continueSpan(span));
            }

            @Override
            public <T> TraceScope startSpan(String name, Sampler<T> s, T info) {
                return new ExceptionCatchingTraceScope(super.startSpan(name, s, info));
            }

            @Override
            public TraceScope startSpan(String name) {
                return new ExceptionCatchingTraceScope(super.startSpan(name));
            }

            @Override
            public TraceScope startSpan(String name, Span parent) {
                return new ExceptionCatchingTraceScope(super.startSpan(name, parent));
            }
        };
    }

    public static class ExceptionCatchingTraceScope extends TraceScope {

        private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

        public ExceptionCatchingTraceScope(TraceScope delegate) {
            super(delegate.getPublisher(), delegate.getSpan(), delegate.getSavedSpan());
        }

        @Override
        public void close() {
            try {
                super.close();
            } catch(Exception e) {
                log.warn("Exception occurred while trying to close span ["+ this + "]", e);
                getSpan().stop();
                if (getSavedSpan() != null
                        && getSpan().getParents().contains(getSavedSpan().getSpanId())) {
                    getPublisher().publishEvent(new SpanReleasedEvent(this, getSavedSpan(),
                            getSpan()));
                }
                else {
                    getPublisher().publishEvent(new SpanReleasedEvent(this, getSpan()));
                }
                TraceContextHolder.setCurrentSpan(getSavedSpan());
            }
        }
    }

}
