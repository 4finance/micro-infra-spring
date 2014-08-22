package com.ofg.infrastructure.web.resttemplate.fluent.head

import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.ResponseTypeRelatedRequestsExecutor
import groovy.transform.TypeChecked
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate

import static org.springframework.http.HttpMethod.HEAD

@TypeChecked
class HeadExecuteForResponseTypeRelated extends ResponseTypeRelatedRequestsExecutor<Object> {

    HeadExecuteForResponseTypeRelated(Map params, RestTemplate restTemplate) {
        super(params, restTemplate, Object)
    }

    @Override
    HttpMethod getHttpMethod() {
        return HEAD
    }

}
