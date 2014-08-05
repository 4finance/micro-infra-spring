package com.ofg.infrastructure.web.filter

import groovy.transform.TypeChecked
import org.springframework.web.filter.Log4jNestedDiagnosticContextFilter

@TypeChecked
class RequestBodyLoggingContextFilter extends Log4jNestedDiagnosticContextFilter {
    RequestBodyLoggingContextFilter(int maxPayloadLength) {
        this.includePayload = true
        this.maxPayloadLength = maxPayloadLength
    }
}
