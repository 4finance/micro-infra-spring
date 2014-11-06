package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import groovy.transform.CompileStatic

/**
 * Abstraction over {@link WithHeaders} with explicitly provided type {@link ResponseReceiving} 
 * so that the compiler resolves types properly
 */
@CompileStatic
class BodyContainingWithHeaders extends WithHeaders<ResponseReceiving> {
    BodyContainingWithHeaders(ResponseReceiving parent, Map<String, String> params, PredefinedHttpHeaders predefinedHeaders) {
        super(parent, params, predefinedHeaders)
    }
}
