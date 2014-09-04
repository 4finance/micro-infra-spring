package com.ofg.infrastructure.web.resttemplate.fluent.put
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.LocationFindingExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.BodyContainingWithHeaders
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersSetting
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ObjectReceiving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ResponseEntityReceiving
import groovy.transform.TypeChecked
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

import static org.springframework.http.HttpMethod.PUT

/**
 * Implementation of the {@link org.springframework.http.HttpMethod#PUT method} fluent API
 */
@TypeChecked
class PutMethodBuilder extends LocationFindingExecutor implements PutMethod, RequestHavingPutMethod, ResponseReceivingPutMethod, UrlParameterizablePutMethod, HeadersSetting {

    public static final String EMPTY_HOST = ''
    
    @Delegate private final BodyContainingWithHeaders withHeaders

    PutMethodBuilder(String host, RestOperations restOperations) {
        super(restOperations)
        params.host = host
        withHeaders = new BodyContainingWithHeaders(this, params)
    }

    PutMethodBuilder(RestOperations restOperations) {
        this(EMPTY_HOST, restOperations)
    }

    @Override
    protected HttpMethod getHttpMethod() {
        return PUT
    }

    @Override
    RequestHavingPutMethod onUrl(URI url) {
        params.url = url
        return this
    }
    
    @Override
    RequestHavingPutMethod onUrl(String url) {
        params.url = new URI(url)
        return this
    }

    @Override
    ResponseReceivingPutMethod httpEntity(HttpEntity httpEntity) {
        params.httpEntity = httpEntity
        return this
    }
    
    @Override
    UrlParameterizablePutMethod onUrlFromTemplate(String urlTemplate) {
        params.urlTemplate = urlTemplate
        return this
    }

    @Override
    RequestHavingPutMethod withVariables(Object... urlVariables) {
        params.urlVariablesArray = urlVariables
        return this
    }

    @Override
    RequestHavingPutMethod withVariables(Map<String, ?> urlVariables) {
        params.urlVariablesMap = urlVariables
        return this
    }

    @Override
    ResponseReceivingPutMethod body(Object request) {
        params.request = request
        return this
    }

    @Override
    ResponseReceivingPutMethod withoutBody() {
        params.request = null
        return this
    }

    @Override
    ObjectReceiving anObject() {
        return new ObjectReceiving() {
            @Override
            public <T> T ofType(Class<T> responseType) {
                return new PutExecuteForResponseTypeRelated<T>(params, restOperations, responseType).exchange()?.body
            }
        }
    }

    @Override
    ResponseEntityReceiving aResponseEntity() {
        return new ResponseEntityReceiving() {
            @Override
            public <T> ResponseEntity<T> ofType(Class<T> responseType) {
                return new PutExecuteForResponseTypeRelated<T>(params, restOperations, responseType).exchange()
            }
        }
    }
    
    @Override
    void ignoringResponse() {
        aResponseEntity().ofType(Object)
    }

}
