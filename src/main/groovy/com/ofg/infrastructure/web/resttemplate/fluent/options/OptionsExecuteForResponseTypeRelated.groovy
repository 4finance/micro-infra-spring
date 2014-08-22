package com.ofg.infrastructure.web.resttemplate.fluent.options
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.ResponseTypeRelatedRequestsExecutor
import groovy.transform.TypeChecked
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate

import static org.springframework.http.HttpMethod.OPTIONS

@TypeChecked
class OptionsExecuteForResponseTypeRelated<T> extends ResponseTypeRelatedRequestsExecutor<T> {

    OptionsExecuteForResponseTypeRelated(Map params, RestTemplate restTemplate, Class<T> responseType) {
        super(params, restTemplate, responseType)
    }

    @Override
    HttpMethod getHttpMethod() {
        return OPTIONS
    }

}
