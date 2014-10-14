package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import groovy.transform.CompileStatic

@CompileStatic
class BodylessWithHeaders<T> extends WithHeaders<T> {
    BodylessWithHeaders(T parent, Map<String, String> params) {
        super(parent, params)
    }
}
