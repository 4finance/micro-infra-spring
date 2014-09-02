package com.ofg.infrastructure.web.resttemplate.fluent.get

import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.ResponseTypeRelatedRequestsExecutor
import groovy.transform.TypeChecked
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestOperations

import static org.springframework.http.HttpMethod.GET

@TypeChecked
class GetExecuteForResponseTypeRelated<T> extends ResponseTypeRelatedRequestsExecutor<T> {

    GetExecuteForResponseTypeRelated(Map params, RestOperations restOperations, Class<T> responseType) {
        super(params, restOperations, responseType)
    }

    @Override
    HttpMethod getHttpMethod() {
        return GET
    }

}
