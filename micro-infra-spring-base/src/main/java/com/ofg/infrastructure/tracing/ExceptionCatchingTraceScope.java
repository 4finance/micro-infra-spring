package com.ofg.infrastructure.tracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.TraceContextHolder;
import org.springframework.cloud.sleuth.TraceScope;
import org.springframework.cloud.sleuth.event.SpanReleasedEvent;

import java.lang.invoke.MethodHandles;

/**
 * Class that wraps TraceScope by catching a thrown exception and logging it.
 *
 * TODO: Remove after fixing in Sleuth
 */
public class ExceptionCatchingTraceScope extends TraceScope {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public ExceptionCatchingTraceScope(TraceScope delegate) {
        super(delegate.getPublisher(), delegate.getSpan(), delegate.getSavedSpan());
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (Exception e) {
            log.warn("Exception occurred while trying to close span [" + this + "]", e);
            stopSpan();
        }
    }

    private void stopSpan() {
        getSpan().stop();
        if (getSavedSpan() != null
                && getSpan().getParents().contains(getSavedSpan().getSpanId())) {
            getPublisher().publishEvent(new SpanReleasedEvent(this, getSavedSpan(),
                    getSpan()));
        } else {
            getPublisher().publishEvent(new SpanReleasedEvent(this, getSpan()));
        }
        TraceContextHolder.setCurrentSpan(getSavedSpan());
    }
}