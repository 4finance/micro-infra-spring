package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

class BodyContainingWithQueryParameters<T> extends WithQueryParameters<T> {
    BodyContainingWithQueryParameters(T parent, Map<String, Object> params) {
        super(parent, params)
    }
}
