package com.ofg.infrastructure.web.resttemplate.fluent.post

import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.ResponseTypeRelatedRequestsExecutor
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate

import static org.springframework.http.HttpMethod.POST

class PostExecuteForResponseTypeRelated<T> extends ResponseTypeRelatedRequestsExecutor<T> {

    PostExecuteForResponseTypeRelated(Map params, RestTemplate restTemplate, Class<T> responseType) {
        super(params, restTemplate, responseType)
    }

    @Override
    HttpMethod getHttpMethod() {
        return POST
    }

}
