package com.ofg.infrastructure.web.resttemplate.fluent.options
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.ResponseTypeRelatedRequestsExecutor
import groovy.transform.CompileStatic
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestOperations

import static org.springframework.http.HttpMethod.OPTIONS
/**
 * Implementation of method execution for the {@link HttpMethod#OPTIONS} method.
 */
@CompileStatic
class OptionsExecuteForResponseTypeRelated<T> extends ResponseTypeRelatedRequestsExecutor<T> {

    OptionsExecuteForResponseTypeRelated(Map params, RestOperations restOperations, Class<T> responseType) {
        super(params, restOperations, responseType)
    }

    @Override
    HttpMethod getHttpMethod() {
        return OPTIONS
    }

}
