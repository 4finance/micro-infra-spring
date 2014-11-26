package com.ofg.infrastructure.web.resttemplate.fluent.put

import com.nurkiewicz.asyncretry.RetryExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.ResponseTypeRelatedRequestsExecutor
import groovy.transform.CompileStatic
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestOperations

import static org.springframework.http.HttpMethod.PUT

/**
 * Implementation of method execution for the {@link HttpMethod#PUT} method.
 */
@CompileStatic
class PutExecuteForResponseTypeRelated<T> extends ResponseTypeRelatedRequestsExecutor<T> {

    PutExecuteForResponseTypeRelated(Map params, RestOperations restOperations, RetryExecutor retryExecutor, Class<T> responseType) {
        super(params, restOperations, retryExecutor, responseType)
    }

    @Override
    HttpMethod getHttpMethod() {
        return PUT
    }

}
