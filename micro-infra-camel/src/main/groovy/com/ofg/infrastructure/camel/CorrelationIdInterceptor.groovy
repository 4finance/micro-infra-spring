package com.ofg.infrastructure.camel

import static com.ofg.infrastructure.web.filter.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER
import com.ofg.infrastructure.web.filter.correlationid.CorrelationIdUpdater
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.apache.camel.Exchange
import org.apache.camel.Processor

@Slf4j
@TypeChecked
class CorrelationIdInterceptor implements Processor {

    @Override
    void process(Exchange exchange) throws Exception {
        String correlationIdHeader = getCorrelationId(exchange)
        CorrelationIdUpdater.updateCorrelationId(correlationIdHeader)
        setCorrelationIdHeaderIfMissing(exchange, correlationIdHeader)
    }

    private String getCorrelationId(Exchange exchange) {
        String correlationIdHeader = exchange.getIn().getHeader(CORRELATION_ID_HEADER)
        if (!correlationIdHeader) {
            log.debug("No correlationId has been set in request inbound message. Creating new one.")
            correlationIdHeader = UUID.randomUUID().toString()
        }
        correlationIdHeader
    }

    private void setCorrelationIdHeaderIfMissing(Exchange exchange, String correlationIdHeader) {
        def inboundMessage = exchange.getIn()
        if (!inboundMessage.headers.containsKey(CORRELATION_ID_HEADER)) {
            log.debug("Setting correlationId [$correlationIdHeader] in header of inbound message")
            inboundMessage.setHeader(CORRELATION_ID_HEADER, correlationIdHeader)
        }
    }

}
