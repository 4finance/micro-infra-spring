package com.ofg.infrastructure.web.logging

import groovy.transform.TypeChecked
import org.springframework.web.filter.Log4jNestedDiagnosticContextFilter

/**
 * Filter that logs request body. To enable it apart from registering it as a filter
 * you have to set DEBUG level of logging. You can also provide the maximum length
 * of the printed payload
 * 
 * @see Log4jNestedDiagnosticContextFilter
 */
@TypeChecked
class RequestBodyLoggingContextFilter extends Log4jNestedDiagnosticContextFilter {
    RequestBodyLoggingContextFilter(int maxPayloadLength) {
        this.includePayload = true
        this.maxPayloadLength = maxPayloadLength
    }
}
