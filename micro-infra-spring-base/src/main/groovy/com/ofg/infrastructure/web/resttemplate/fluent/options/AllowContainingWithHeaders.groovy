package com.ofg.infrastructure.web.resttemplate.fluent.options

import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.WithHeaders
import groovy.transform.TypeChecked

/**
 * Class that provides explicit types for the {@link WithHeaders} so that the compiler
 * know what the types it should return
 */
@TypeChecked
class AllowContainingWithHeaders extends WithHeaders<ResponseReceivingOptionsMethod> {
    AllowContainingWithHeaders(ResponseReceivingOptionsMethod parent, Map<String, String> params) {
        super(parent, params)
    }
}
