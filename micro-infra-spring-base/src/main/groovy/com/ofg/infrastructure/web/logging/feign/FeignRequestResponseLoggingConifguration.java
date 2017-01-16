package com.ofg.infrastructure.web.logging.feign;

import com.ofg.infrastructure.tracing.EnableTracing;
import com.ofg.infrastructure.web.logging.FeignCallObfuscatingLogger;
import com.ofg.infrastructure.web.logging.RequestDataProvider;
import com.ofg.infrastructure.web.logging.RequestResponseLogger;
import com.ofg.infrastructure.web.logging.RequestIdProvider;
import com.ofg.infrastructure.web.logging.config.LogsConfig;
import feign.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.sleuth.SpanAccessor;
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableTracing
@ConditionalOnProperty(name = "rest.client.feign.logging.enabled", matchIfMissing = true)
@AutoConfigureAfter(TraceAutoConfiguration.class)
public class FeignRequestResponseLoggingConifguration {

    @Bean
    public RequestIdProvider traceIdProvider(SpanAccessor spanAccessor) {
        return new SleuthRequestIdProvider(spanAccessor);
    }
    
    @Bean
    public Logger feignLogger(LogsConfig props, RequestDataProvider requestDataProvider,
                              RequestIdProvider traceIdProvider, RequestResponseLogger requestResponseLogger) {
        return new FeignCallObfuscatingLogger(props, requestDataProvider, traceIdProvider, requestResponseLogger);
    }
    
    @Bean
    public RequestDataProvider requestDataProvider(@Value("${rest.client.feign.logging.payload.expirationTimeout:20000}") int requestDataExpirationTimeout) {
        return new RequestDataProvider(requestDataExpirationTimeout);
    }
}
