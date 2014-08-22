package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

class BodyContainingWithHeaders extends WithHeaders<ResponseReceiving> {
    BodyContainingWithHeaders(ResponseReceiving parent, Map<String, String> params) {
        super(parent, params)
    }
}
