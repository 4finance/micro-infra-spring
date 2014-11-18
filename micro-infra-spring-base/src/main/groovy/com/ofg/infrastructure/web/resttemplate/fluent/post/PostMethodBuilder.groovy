package com.ofg.infrastructure.web.resttemplate.fluent.post

import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.LocationFindingExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.BodyContainingWithHeaders
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersSetting
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ObjectReceiving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ResponseEntityReceiving
import groovy.transform.TypeChecked
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

import static org.springframework.http.HttpMethod.POST
import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders.NO_PREDEFINED_HEADERS

/**
 * Implementation of the {@link org.springframework.http.HttpMethod#POST method} fluent API
 */
@TypeChecked
class PostMethodBuilder extends LocationFindingExecutor implements
        PostMethod, RequestHavingPostMethod, ResponseReceivingPostMethod,
        UrlParameterizablePostMethod, HeadersSetting {

    public static final String EMPTY_HOST = ''
    
    @Delegate private final BodyContainingWithHeaders withHeaders

    PostMethodBuilder(String host, RestOperations restOperations, PredefinedHttpHeaders predefinedHeaders) {
        super(restOperations)
        params.host = host
        withHeaders = new BodyContainingWithHeaders(this, params, predefinedHeaders)
    }
    
    PostMethodBuilder(RestOperations restOperations) {
        this(EMPTY_HOST, restOperations, NO_PREDEFINED_HEADERS)
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
    ResponseReceivingPostMethod httpEntity(HttpEntity httpEntity) {
        params.httpEntity = httpEntity
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
        Object requestToSet = (request instanceof GString ? request.toString() : request)
        params.request = requestToSet
        return this
    }

    @Override
    ResponseReceivingPostMethod withoutBody() {
        params.request = null
        return this
    }

    @Override
    ObjectReceiving anObject() {
        return new ObjectReceiving() {
            @Override
            public <T> T ofType(Class<T> responseType) {
                return new PostExecuteForResponseTypeRelated<T>(params, restOperations, responseType).exchange()?.body
            }
        }
    }

    @Override
    ResponseEntityReceiving aResponseEntity() {
        return new ResponseEntityReceiving() {
            @Override
            public <T> ResponseEntity<T> ofType(Class<T> responseType) {
                return new PostExecuteForResponseTypeRelated<T>(params, restOperations, responseType).exchange()
            }
        }
    }
    
    @Override
    void ignoringResponse() {
        aResponseEntity().ofType(Object)    
    }

}
