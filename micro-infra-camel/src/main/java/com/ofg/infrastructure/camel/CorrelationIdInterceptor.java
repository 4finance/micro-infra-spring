package com.ofg.infrastructure.camel;

import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER;
import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.OLD_CORRELATION_ID_HEADER;
import static org.springframework.cloud.sleuth.Span.PARENT_ID_NAME;
import static org.springframework.cloud.sleuth.Span.PROCESS_ID_NAME;
import static org.springframework.cloud.sleuth.Span.SPAN_ID_NAME;
import static org.springframework.cloud.sleuth.Span.SPAN_NAME_NAME;
import static org.springframework.cloud.sleuth.Span.builder;

import java.lang.invoke.MethodHandles;
import java.util.Random;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import com.google.common.collect.Iterables;

/**
 * Interceptor class that ensures the correlationId header is present in {@Exchange}.
 */
public class CorrelationIdInterceptor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Tracer trace;

    private final Random idGenerator;

    public CorrelationIdInterceptor(Random idGenerator, Tracer trace) {
        this.trace = trace;
        this.idGenerator = idGenerator;
    }

    /**
     * Ensures correlationId header is set in incoming message (if is missing a new correlationId is created and set).
     *
     * @param exchange Camel's container holding received message
     * @throws Exception if an internal processing error has occurred
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        Span span = getCorrelationId(exchange);
        trace.continueSpan(span);
        setCorrelationIdHeaderIfMissing(exchange, span);
    }

    private Span getCorrelationId(Exchange exchange) {
        Long traceId = (Long) exchange.getIn().getHeader(CORRELATION_ID_HEADER);
        Long oldTraceId = (Long) exchange.getIn().getHeader(OLD_CORRELATION_ID_HEADER);
        Long spanId = (Long) exchange.getIn().getHeader(SPAN_ID_NAME);
        String notSampledName = (String) exchange.getIn().getHeader(SPAN_NAME_NAME);
        Long parentId = (Long) exchange.getIn().getHeader(PARENT_ID_NAME);
        String processID = (String) exchange.getIn().getHeader(PROCESS_ID_NAME);
        if (traceId == null && oldTraceId == null) {
            log.debug("No correlationId has been set in request inbound message. Creating new one.");
            traceId = idGenerator.nextLong();
        }
        if (spanId == null) {
            log.debug("No spanId has been set in request inbound message. Creating new one.");
            spanId = idGenerator.nextLong();
        }
        return builder().spanId(spanId).traceId(firstNonNull(oldTraceId, traceId))
                .name(notSampledName).parent(parentId).processId(processID).build();
    }

    private Long firstNonNull(Long first, Long second) {
        if (first != null) {
            return first;
        }
        return second;
    }

    private void setCorrelationIdHeaderIfMissing(Exchange exchange, Span span) {
        Message inboundMessage = exchange.getIn();
        if (!inboundMessage.getHeaders().containsKey(OLD_CORRELATION_ID_HEADER)) {
            log.debug("Setting correlationId [{}] in header of inbound message", span.getTraceId());
            inboundMessage.setHeader(SPAN_ID_NAME, span.getSpanId());
            inboundMessage.setHeader(CORRELATION_ID_HEADER, span.getTraceId());
            inboundMessage.setHeader(OLD_CORRELATION_ID_HEADER, span.getTraceId());
            inboundMessage.setHeader(SPAN_NAME_NAME, span.getName());
            inboundMessage.setHeader(PARENT_ID_NAME, Iterables.getFirst(span.getParents(), null));
            inboundMessage.setHeader(PROCESS_ID_NAME, span.getProcessId());
        }

    }
}
