package com.ofg.infrastructure.camel

import groovy.transform.CompileStatic
import org.apache.camel.Message

import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER
import com.ofg.infrastructure.correlationid.CorrelationIdUpdater
import groovy.util.logging.Slf4j
import org.apache.camel.Exchange
import org.apache.camel.Processor

/**
 * Interceptor class that ensures the correlationId header is present in {@Exchange}.
 */
@Slf4j
@CompileStatic
class CorrelationIdInterceptor implements Processor {

    /**
     * Ensures correlationId header is set in incoming message (if is missing a new correlationId is created and set).
     *
     * @param exchange Camel's container holding received message
     *
     * @throws Exception if an internal processing error has occurred
     */
    @Override
    void process(Exchange exchange) throws Exception {
        String correlationIdHeader = getCorrelationId(exchange)
        CorrelationIdUpdater.updateCorrelationId(correlationIdHeader)
        setCorrelationIdHeaderIfMissing(exchange, correlationIdHeader)
    }

    private String getCorrelationId(Exchange exchange) {
        String correlationIdHeader = exchange.in.getHeader(CORRELATION_ID_HEADER)
        if (!correlationIdHeader) {
            log.debug('No correlationId has been set in request inbound message. Creating new one.')
            correlationIdHeader = UUID.randomUUID().toString()
        }
        return correlationIdHeader
    }

    private void setCorrelationIdHeaderIfMissing(Exchange exchange, String correlationIdHeader) {
        Message inboundMessage = exchange.in
        if (!inboundMessage.headers.containsKey(CORRELATION_ID_HEADER)) {
            log.debug("Setting correlationId [$correlationIdHeader] in header of inbound message")
            inboundMessage.setHeader(CORRELATION_ID_HEADER, correlationIdHeader)
        }
    }

}
