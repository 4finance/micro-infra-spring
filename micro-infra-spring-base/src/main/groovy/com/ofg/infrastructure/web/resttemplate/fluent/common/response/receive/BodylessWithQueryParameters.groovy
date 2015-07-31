package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import groovy.transform.CompileStatic

@CompileStatic
class BodylessWithQueryParameters<T> extends WithQueryParameters<T> {
    BodylessWithQueryParameters(T parent, Map<String,Object> params) {
        super(parent, params)
    }
}
