package com.ofg.infrastructure.web.resttemplate.fluent.get

import com.nurkiewicz.asyncretry.RetryExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.ResponseTypeRelatedRequestsExecutor
import groovy.transform.CompileStatic
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestOperations

import static org.springframework.http.HttpMethod.GET
/**
 * Implementation of method execution for the {@link HttpMethod#GET} method.
 * TODO Does this have to be a subclass?
 */
@CompileStatic
class GetExecuteForResponseTypeRelated<T> extends ResponseTypeRelatedRequestsExecutor<T> {

    GetExecuteForResponseTypeRelated(Map params, RestOperations restOperations, RetryExecutor retryExecutor, Class<T> responseType) {
        super(params, restOperations, retryExecutor, responseType)
    }

    @Override
    HttpMethod getHttpMethod() {
        return GET
    }

}
