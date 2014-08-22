package com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor

import groovy.transform.TypeChecked
import org.springframework.http.HttpMethod as SpringHttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.HttpEntityUtils.getHttpEntityFrom

@TypeChecked
abstract class ResponseTypeRelatedRequestsExecutor<T> {

    protected final RestTemplate restTemplate
    protected final Map params
    protected final Class<T> responseType

    ResponseTypeRelatedRequestsExecutor(Map params, RestTemplate restTemplate, Class<T> responseType) {
        this.restTemplate = restTemplate
        this.params = params
        this.responseType = responseType
    }

    protected abstract SpringHttpMethod getHttpMethod()

    ResponseEntity<T> exchange() {
        if (params.url) {
            return restTemplate.exchange(params.url as URI, getHttpMethod(), getHttpEntityFrom(params), responseType)
        } else if (params.urlTemplate) {
            return restTemplate.exchange("${params.host}${params.urlTemplate}", getHttpMethod(), getHttpEntityFrom(params), responseType, params.urlVariablesArray as Object[] ?: params.urlVariablesMap as Map<String, ?>)
        }
        throw new InvalidHttpMethodParametersException(params)
    }
    
}

