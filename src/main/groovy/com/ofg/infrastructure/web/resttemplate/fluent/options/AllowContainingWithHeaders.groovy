package com.ofg.infrastructure.web.resttemplate.fluent.options

import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.WithHeaders
import groovy.transform.TypeChecked

@TypeChecked
class AllowContainingWithHeaders extends WithHeaders<ResponseReceivingOptionsMethod> {
    AllowContainingWithHeaders(ResponseReceivingOptionsMethod parent, Map<String, String> params) {
        super(parent, params)
    }
}
