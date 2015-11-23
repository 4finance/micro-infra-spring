package com.ofg.infrastructure.correlationid;

import groovy.transform.CompileStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.TraceContextHolder;

import java.lang.invoke.MethodHandles;

/**
 * Class that takes care of updating all necessary components with new value
 * of correlation id.
 * It sets correlationId on {@link ThreadLocal} in {@link CorrelationIdHolder}
 * and in {@link MDC}.
 *
 * @see CorrelationIdHolder
 * @see MDC
 */
@CompileStatic
public class CorrelationIdUpdater {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void updateCorrelationId(Span span) {
        if (span == null) {
            log.debug("There is no span to update. Clearing up the current context");
            TraceContextHolder.removeCurrentSpan();
            return;

        }
        log.debug("Updating correlationId with value: [" + span.getTraceId() + "]");
        TraceContextHolder.setCurrentSpan(span);
        MDC.put(CorrelationIdHolder.CORRELATION_ID_HEADER, span.getTraceId());
    }

}
