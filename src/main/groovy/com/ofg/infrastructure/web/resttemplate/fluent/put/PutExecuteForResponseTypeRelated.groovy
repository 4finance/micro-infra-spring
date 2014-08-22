package com.ofg.infrastructure.web.resttemplate.fluent.put

import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.ResponseTypeRelatedRequestsExecutor
import groovy.transform.TypeChecked
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate

import static org.springframework.http.HttpMethod.PUT

@TypeChecked
class PutExecuteForResponseTypeRelated<T> extends ResponseTypeRelatedRequestsExecutor<T> {

    PutExecuteForResponseTypeRelated(Map params, RestTemplate restTemplate, Class<T> responseType) {
        super(params, restTemplate, responseType)
    }

    @Override
    HttpMethod getHttpMethod() {
        return PUT
    }

}
