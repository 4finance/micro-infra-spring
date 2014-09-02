package com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor

import groovy.transform.TypeChecked
import org.springframework.http.HttpMethod as SpringHttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.HttpEntityUtils.getHttpEntityFrom
import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.UrlParsingUtils.appendPathToHost

@TypeChecked
abstract class ResponseTypeRelatedRequestsExecutor<T> {

    protected final RestOperations restOperations
    protected final Map params
    protected final Class<T> responseType

    ResponseTypeRelatedRequestsExecutor(Map params, RestOperations restOperations, Class<T> responseType) {
        this.restOperations = restOperations
        this.params = params
        this.responseType = responseType
    }

    protected abstract SpringHttpMethod getHttpMethod()

    ResponseEntity<T> exchange() {
        if (params.url) {  
            return restOperations.exchange(new URI(appendPathToHost(params.host as String, params.url as URI)), getHttpMethod(), getHttpEntityFrom(params), responseType)
        } else if (params.urlTemplate) {
            return restOperations.exchange(appendPathToHost(params.host as String, params.urlTemplate as String), getHttpMethod(), getHttpEntityFrom(params), responseType, params.urlVariablesArray as Object[] ?: params.urlVariablesMap as Map<String, ?>)
        }
        throw new InvalidHttpMethodParametersException(params)
    }
    
}

