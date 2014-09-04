package com.ofg.infrastructure.web.resttemplate.fluent.options
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.InvalidHttpMethodParametersException
import groovy.transform.PackageScope
import groovy.transform.TypeChecked
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestOperations

import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.HttpEntityUtils.getHttpEntityFrom
import static org.springframework.http.HttpMethod.OPTIONS

/**
 * Class that executes {@link RestOperations}.exchange() and from {@link org.springframework.http.ResponseEntity#headers}
 * it returns {@link org.springframework.http.HttpHeaders#ALLOW} header
 */
@TypeChecked 
@PackageScope 
class OptionsAllowHeaderExecutor implements AllowHeaderReceiving {

    private final Map params
    private final RestOperations restOperations

    OptionsAllowHeaderExecutor(Map params, RestOperations restOperations) {
        this.params = params
        this.restOperations = restOperations
    }

    @Override
    Set<HttpMethod> allow() {
        if(params.url) {
            return restOperations.exchange(params.url as URI, OPTIONS, getHttpEntityFrom(params), Object).headers.getAllow()
        } else if(params.urlTemplate) {
            return restOperations.exchange("${params.host}${params.urlTemplate}", OPTIONS, getHttpEntityFrom(params), Object, params.urlVariablesArray as Object[] ?: params.urlVariablesMap as Map<String, ?>).headers.getAllow()
        }
        throw new InvalidHttpMethodParametersException(params)
    }
}
