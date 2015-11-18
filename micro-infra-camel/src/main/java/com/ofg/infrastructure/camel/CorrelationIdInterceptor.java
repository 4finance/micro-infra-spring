package com.ofg.infrastructure.camel;

import com.google.common.collect.Iterables;
import com.ofg.infrastructure.correlationid.CorrelationIdUpdater;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.IdGenerator;
import org.springframework.cloud.sleuth.MilliSpan;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Trace;

import java.lang.invoke.MethodHandles;

/**
 * Interceptor class that ensures the correlationId header is present in {@Exchange}.
 */
public class CorrelationIdInterceptor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final IdGenerator idGenerator;

    public CorrelationIdInterceptor(IdGenerator idGenerator) {
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
        CorrelationIdUpdater.updateCorrelationId(span);
        setCorrelationIdHeaderIfMissing(exchange, span);
    }

    private Span getCorrelationId(Exchange exchange) {
        String traceId = (String) exchange.getIn().getHeader(Trace.TRACE_ID_NAME);
        String spanId = (String) exchange.getIn().getHeader(Trace.SPAN_ID_NAME);
        String notSampledName = (String) exchange.getIn().getHeader(Trace.SPAN_NAME_NAME);
        String parentId = (String) exchange.getIn().getHeader(Trace.PARENT_ID_NAME);
        String processID = (String) exchange.getIn().getHeader(Trace.PROCESS_ID_NAME);
        if (traceId == null) {
            log.debug("No correlationId has been set in request inbound message. Creating new one.");
            traceId = idGenerator.create();
        }
        return MilliSpan.builder().spanId(spanId).traceId(traceId).name(notSampledName).parent(parentId).processId(processID).build();
    }

    private void setCorrelationIdHeaderIfMissing(Exchange exchange, Span span) {
        Message inboundMessage = exchange.getIn();
        if (!inboundMessage.getHeaders().containsKey(Trace.SPAN_ID_NAME)) {
            log.debug("Setting correlationId [{}] in header of inbound message", span.getSpanId());
            inboundMessage.setHeader(Trace.SPAN_ID_NAME, span.getSpanId());
            inboundMessage.setHeader(Trace.TRACE_ID_NAME, span.getTraceId());
            inboundMessage.setHeader(Trace.SPAN_NAME_NAME, span.getName());
            inboundMessage.setHeader(Trace.PARENT_ID_NAME, Iterables.getFirst(span.getParents(), null));
            inboundMessage.setHeader(Trace.PROCESS_ID_NAME, span.getProcessId());
        }

    }
}
