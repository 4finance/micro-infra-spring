package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

class BodylessWithHeaders<T> extends WithHeaders<T> {
    BodylessWithHeaders(T parent, Map<String, String> params) {
        super(parent, params)
    }
}
