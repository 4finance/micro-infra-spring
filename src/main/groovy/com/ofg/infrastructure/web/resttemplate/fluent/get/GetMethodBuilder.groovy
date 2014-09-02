package com.ofg.infrastructure.web.resttemplate.fluent.get

import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.BodyContainingWithHeaders
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersHaving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ObjectReceiving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ResponseEntityReceiving
import groovy.transform.TypeChecked
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

@TypeChecked
class GetMethodBuilder implements GetMethod, UrlParameterizableGetMethod, ResponseReceivingGetMethod, HeadersHaving {

    public static final String EMPTY_HOST = ''

    private final Map params = [:]
    private final RestOperations restOperations
    @Delegate private final BodyContainingWithHeaders withHeaders

    GetMethodBuilder(String host, RestOperations restOperations) {
        this.restOperations = restOperations
        params.host = host
        withHeaders = new BodyContainingWithHeaders(this, params)
    }

    GetMethodBuilder(RestOperations restOperations) {
        this(EMPTY_HOST, restOperations)
    }

    @Override
    ResponseReceivingGetMethod onUrl(URI url) {
        params.url = url
        return this
    }
    
    @Override
    ResponseReceivingGetMethod onUrl(String url) {
        params.url = new URI(url)
        return this
    }
    
    @Override
    ResponseReceivingGetMethod httpEntity(HttpEntity httpEntity) {
        params.httpEntity = httpEntity
        return this
    }

    @Override
    UrlParameterizableGetMethod onUrlFromTemplate(String urlTemplate) {
        params.urlTemplate = urlTemplate
        return this
    }

    @Override
    ResponseReceivingGetMethod withVariables(Object... urlVariables) {
        params.urlVariablesArray = urlVariables
        return this
    }

    @Override
    ResponseReceivingGetMethod withVariables(Map<String, ?> urlVariables) {
        params.urlVariablesMap = urlVariables
        return this
    }

    @Override
    ObjectReceiving anObject() {
        return new ObjectReceiving() {
            @Override
            public <T> T ofType(Class<T> responseType) {
                return new GetExecuteForResponseTypeRelated<T>(params, restOperations, responseType).exchange()?.body
            }
        }
    }

    @Override
    ResponseEntityReceiving aResponseEntity() {
        return new ResponseEntityReceiving() {
            @Override
            public <T> ResponseEntity<T> ofType(Class<T> responseType) {
                return new GetExecuteForResponseTypeRelated<T>(params, restOperations, responseType).exchange()
            }
        }
    }

    @Override
    void ignoringResponse() {
        aResponseEntity().ofType(Object)
    }

}
