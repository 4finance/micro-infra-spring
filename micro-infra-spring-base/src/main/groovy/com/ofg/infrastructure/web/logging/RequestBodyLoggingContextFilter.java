package com.ofg.infrastructure.web.logging;

import org.springframework.web.filter.Log4jNestedDiagnosticContextFilter;

/**
 * Filter that logs request body. To enable it apart from registering it as a filter
 * you have to set DEBUG level of logging. You can also provide the maximum length
 * of the printed payload
 *
 * @see Log4jNestedDiagnosticContextFilter
 */
public class RequestBodyLoggingContextFilter extends Log4jNestedDiagnosticContextFilter {
    public RequestBodyLoggingContextFilter(int maxPayloadLength) {
        this.setIncludePayload(true);
        this.setMaxPayloadLength(maxPayloadLength);
    }
}
