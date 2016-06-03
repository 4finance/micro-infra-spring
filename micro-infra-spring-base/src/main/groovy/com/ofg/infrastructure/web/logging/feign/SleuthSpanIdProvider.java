package com.ofg.infrastructure.web.logging.feign;


import com.ofg.infrastructure.web.logging.SpanIdProvider;
import org.springframework.cloud.sleuth.SpanAccessor;

public class SleuthSpanIdProvider implements SpanIdProvider {
    
    private final SpanAccessor spanAccessor;

    public SleuthSpanIdProvider(SpanAccessor spanAccessor) {
        this.spanAccessor = spanAccessor;
    }

    @Override
    public String getSpanId() {
        return String.valueOf(spanAccessor.getCurrentSpan().getSpanId());
    }
}
