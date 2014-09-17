package com.ofg.infrastructure.web.filter.correlationid

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.slf4j.MDC
import org.springframework.util.StringUtils

import static com.ofg.infrastructure.web.filter.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER

/**
 * Class that takes care of updating all necessary components with new value
 * of correlation id.
 * It sets correlationId on {@link ThreadLocal} in {@link CorrelationIdHolder}
 * and in {@link MDC}.
 *
 * @see CorrelationIdHolder
 * @see MDC
 */
@Slf4j
@CompileStatic
class CorrelationIdUpdater {

    static void updateCorrelationId(String correlationId) {
        if (StringUtils.hasText(correlationId)) {
            log.debug("Updating correlationId with value: [$correlationId]")
            CorrelationIdHolder.set(correlationId)
            MDC.put(CORRELATION_ID_HEADER, correlationId)
        }
    }

}
