package com.ofg.infrastructure.web.logging.feign;


import com.ofg.infrastructure.web.logging.RequestIdProvider;
import org.springframework.cloud.sleuth.SpanAccessor;

public class SleuthRequestIdProvider implements RequestIdProvider {
    
    private final SpanAccessor spanAccessor;

    public SleuthRequestIdProvider(SpanAccessor spanAccessor) {
        this.spanAccessor = spanAccessor;
    }

    @Override
    public String getRequestId() {
        return String.valueOf(spanAccessor.getCurrentSpan().getSpanId());
    }
}
