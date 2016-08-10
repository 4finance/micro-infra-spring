package com.ofg.infrastructure.web.logging.feign;

import com.ofg.infrastructure.tracing.EnableTracing;
import com.ofg.infrastructure.web.logging.FeignCallObfuscatingLogger;
import com.ofg.infrastructure.web.logging.RequestDataProvider;
import com.ofg.infrastructure.web.logging.RequestResponseLogger;
import com.ofg.infrastructure.web.logging.SpanIdProvider;
import feign.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.sleuth.SpanAccessor;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableTracing
@ConditionalOnProperty(name = "rest.client.feign.logging.enabled", matchIfMissing = true)
@AutoConfigureAfter(TraceAutoConfiguration.class)
public class FeignRequestResponseLoggingConifguration {

    @Bean
    public SpanIdProvider traceIdProvider(SpanAccessor spanAccessor) {
        return new SleuthSpanIdProvider(spanAccessor);
    }
    
    @Bean
    public Logger feignLogger(RequestDataProvider requestDataProvider, SpanIdProvider traceIdProvider, RequestResponseLogger requestResponseLogger) {
        return new FeignCallObfuscatingLogger(requestDataProvider, traceIdProvider, requestResponseLogger);
    }
    
    @Bean
    public RequestDataProvider requestDataProvider(@Value("${rest.client.feign.logging.payload.expirationTimeout:5000}") int requestDataExpirationTimeout) {
        return new RequestDataProvider(requestDataExpirationTimeout);
    }
}
