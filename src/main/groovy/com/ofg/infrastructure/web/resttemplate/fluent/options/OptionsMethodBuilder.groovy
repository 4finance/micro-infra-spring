package com.ofg.infrastructure.web.resttemplate.fluent.options

import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersHaving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ObjectReceiving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ResponseEntityReceiving
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

class OptionsMethodBuilder implements OptionsMethod, UrlParameterizableOptionsMethod, ResponseReceivingOptionsMethod, HeadersHaving, AllowHeaderReceiving {

    public static final String EMPTY_HOST = ''

    private final Map params = [:]
    private final RestTemplate restTemplate
    @Delegate private final AllowContainingWithHeaders withHeaders
    @Delegate private final OptionsAllowHeaderExecutor allowHeaderExecutor

    OptionsMethodBuilder(String host, RestTemplate restTemplate) {
        this.restTemplate = restTemplate
        params.host = host
        withHeaders = new AllowContainingWithHeaders(this, params)
        allowHeaderExecutor = new OptionsAllowHeaderExecutor(params, restTemplate)
    }

    OptionsMethodBuilder(RestTemplate restTemplate) {
        this(EMPTY_HOST, restTemplate)
    }

    @Override
    ResponseReceivingOptionsMethod onUrl(URI url) {
        params.url = url
        return this
    }

    @Override
    UrlParameterizableOptionsMethod onUrlFromTemplate(String urlTemplate) {
        params.urlTemplate = urlTemplate
        return this
    }

    @Override
    ResponseReceivingOptionsMethod withVariables(Object... urlVariables) {
        params.urlVariablesArray = urlVariables
        return this
    }

    @Override
    ResponseReceivingOptionsMethod withVariables(Map<String, ?> urlVariables) {
        params.urlVariablesMap = urlVariables
        return this
    }

    @Override
    Set<HttpMethod> allow() {
        return allowHeaderExecutor.allow()
    }

    @Override
    ObjectReceiving anObject() {
        return new ObjectReceiving() {
            @Override
            public <T> T ofType(Class<T> responseType) {
                return new OptionsExecuteForResponseTypeRelated<T>(params, restTemplate, responseType).exchange()?.body
            }
        }
    }

    @Override
    ResponseEntityReceiving aResponseEntity() {
        return new ResponseEntityReceiving() {
            @Override
            public <T> ResponseEntity<T> ofType(Class<T> responseType) {
                return new OptionsExecuteForResponseTypeRelated<T>(params, restTemplate, responseType).exchange()
            }
        }
    }

}
