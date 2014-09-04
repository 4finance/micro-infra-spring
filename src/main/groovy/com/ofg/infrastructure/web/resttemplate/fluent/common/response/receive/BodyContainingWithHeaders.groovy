package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import groovy.transform.TypeChecked

/**
 * Abstraction over {@link WithHeaders} with explicitly provided type {@link ResponseReceiving} 
 * so that the compiler resolves types properly
 */
@TypeChecked
class BodyContainingWithHeaders extends WithHeaders<ResponseReceiving> {
    BodyContainingWithHeaders(ResponseReceiving parent, Map<String, String> params) {
        super(parent, params)
    }
}
