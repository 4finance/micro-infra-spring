package com.ofg.infrastructure.web.resttemplate.fluent.post

import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.LocationFindingExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.BodyContainingWithHeaders
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersSetting
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ObjectReceiving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ResponseEntityReceiving
import groovy.transform.TypeChecked
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

import static org.springframework.http.HttpMethod.POST

@TypeChecked
class PostMethodBuilder extends LocationFindingExecutor implements PostMethod, RequestHavingPostMethod, ResponseReceivingPostMethod, UrlParameterizablePostMethod, HeadersSetting {

    public static final String EMPTY_HOST = ''
    
    @Delegate private final BodyContainingWithHeaders withHeaders

    PostMethodBuilder(String host, RestTemplate restTemplate) {
        super(restTemplate)
        params.host = host
        withHeaders = new BodyContainingWithHeaders(this, params)
    }
    
    PostMethodBuilder(RestTemplate restTemplate) {
        this(EMPTY_HOST, restTemplate)
    }

    @Override
    protected HttpMethod getHttpMethod() {
        return POST
    }

    @Override
    RequestHavingPostMethod onUrl(URI url) {
        params.url = url
        return this
    }

    @Override
    RequestHavingPostMethod onUrl(String url) {
        params.url = new URI(url)
        return this
    }

    @Override
    UrlParameterizablePostMethod onUrlFromTemplate(String urlTemplate) {
        params.urlTemplate = urlTemplate
        return this
    }

    @Override
    RequestHavingPostMethod withVariables(Object... urlVariables) {
        params.urlVariablesArray = urlVariables
        return this
    }

    @Override
    RequestHavingPostMethod withVariables(Map<String, ?> urlVariables) {
        params.urlVariablesMap = urlVariables
        return this
    }

    @Override
    ResponseReceivingPostMethod body(Object request) {
        params.request = request
        return this
    }

    @Override
    ObjectReceiving anObject() {
        return new ObjectReceiving() {
            @Override
            public <T> T ofType(Class<T> responseType) {
                return new PostExecuteForResponseTypeRelated<T>(params, restTemplate, responseType).exchange()?.body
            }
        }
    }

    @Override
    ResponseEntityReceiving aResponseEntity() {
        return new ResponseEntityReceiving() {
            @Override
            public <T> ResponseEntity<T> ofType(Class<T> responseType) {
                return new PostExecuteForResponseTypeRelated<T>(params, restTemplate, responseType).exchange()
            }
        }
    }

}
