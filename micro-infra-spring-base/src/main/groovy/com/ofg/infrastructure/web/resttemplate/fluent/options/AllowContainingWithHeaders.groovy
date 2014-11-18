package com.ofg.infrastructure.web.resttemplate.fluent.options

import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.WithHeaders
import groovy.transform.CompileStatic

/**
 * Class that provides explicit types for the {@link WithHeaders} so that the compiler
 * know what the types it should return
 */
@CompileStatic
class AllowContainingWithHeaders extends WithHeaders<ResponseReceivingOptionsMethod> {
    AllowContainingWithHeaders(ResponseReceivingOptionsMethod parent, Map<String, String> params, PredefinedHttpHeaders predefinedHeaders) {
        super(parent, params, predefinedHeaders)
    }
}
