package com.ofg.infrastructure.web.resttemplate.fluent.options
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.InvalidHttpMethodParametersException
import groovy.transform.PackageScope
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate

import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.HttpEntityUtils.getHttpEntityFrom
import static org.springframework.http.HttpMethod.OPTIONS

@PackageScope class OptionsAllowHeaderExecutor implements AllowHeaderReceiving {

    private final Map params
    private final RestTemplate restTemplate

    OptionsAllowHeaderExecutor(Map params, RestTemplate restTemplate) {
        this.params = params
        this.restTemplate = restTemplate
    }

    @Override
    Set<HttpMethod> allow() {
        if(params.url) {
            return restTemplate.exchange(params.url as URI, OPTIONS, getHttpEntityFrom(params), Object).headers.getAllow()
        } else if(params.urlTemplate) {
            return restTemplate.exchange("${params.host}${params.urlTemplate}", OPTIONS, getHttpEntityFrom(params), Object, params.urlVariablesArray as Object[] ?: params.urlVariablesMap as Map<String, ?>).headers.getAllow()
        }
        throw new InvalidHttpMethodParametersException(params)
    }
}
