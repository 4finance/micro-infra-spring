package com.ofg.infrastructure.web.resttemplate.fluent.post

import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.ResponseTypeRelatedRequestsExecutor
import groovy.transform.TypeChecked
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestOperations

import static org.springframework.http.HttpMethod.POST

@TypeChecked
class PostExecuteForResponseTypeRelated<T> extends ResponseTypeRelatedRequestsExecutor<T> {

    PostExecuteForResponseTypeRelated(Map params, RestOperations restOperations, Class<T> responseType) {
        super(params, restOperations, responseType)
    }

    @Override
    HttpMethod getHttpMethod() {
        return POST
    }

}
