package com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor

import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate

import static HttpEntityUtils.getHttpEntityFrom

abstract class LocationFindingExecutor implements LocationReceiving {

    protected final Map params = [:]
    protected final RestTemplate restTemplate

    LocationFindingExecutor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate
    }

    protected abstract HttpMethod getHttpMethod()

    @Override
    URI forLocation() {
        if(params.url) {
            return restTemplate.exchange(params.url as URI, getHttpMethod(), getHttpEntityFrom(params), params.request.class).headers.getLocation()
        } else if(params.urlTemplate) {
            return restTemplate.exchange("${params.host}${params.urlTemplate}", getHttpMethod(), getHttpEntityFrom(params), params.request.class, params.urlVariablesArray as Object[] ?: params.urlVariablesMap as Map<String, ?>).headers.getLocation()
        }
        throw new InvalidHttpMethodParametersException(params)
    }
}
