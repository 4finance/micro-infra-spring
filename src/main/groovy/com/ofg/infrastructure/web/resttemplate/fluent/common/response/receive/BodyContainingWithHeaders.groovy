package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import groovy.transform.TypeChecked

@TypeChecked
class BodyContainingWithHeaders extends WithHeaders<ResponseReceiving> {
    BodyContainingWithHeaders(ResponseReceiving parent, Map<String, String> params) {
        super(parent, params)
    }
}
