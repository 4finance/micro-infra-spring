package com.ofg.infrastructure.web.resttemplate.fluent.put

import com.google.common.base.Function
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.nurkiewicz.asyncretry.RetryExecutor
import com.nurkiewicz.asyncretry.SyncRetryExecutor
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

import static org.springframework.http.HttpMethod.PUT

import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders.NO_PREDEFINED_HEADERS

/**
 * Implementation of the {@link org.springframework.http.HttpMethod#PUT method} fluent API
 */
@TypeChecked
class PutMethodBuilder extends LocationFindingExecutor implements
        PutMethod, RequestHavingPutMethod, ResponseReceivingPutMethod,
        UrlParameterizablePutMethod, HeadersSetting {

    public static final String EMPTY_HOST = ''
    
    @Delegate private final BodyContainingWithHeaders withHeaders

    PutMethodBuilder(String host, RestOperations restOperations, PredefinedHttpHeaders predefinedHeaders, RetryExecutor retryExecutor) {
        super(restOperations, retryExecutor)
        params.host = host
        withHeaders = new BodyContainingWithHeaders(this, params, predefinedHeaders)
    }

    PutMethodBuilder(RestOperations restOperations) {
        this(EMPTY_HOST, restOperations, NO_PREDEFINED_HEADERS, SyncRetryExecutor.INSTANCE)
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
        Object requestToSet = (request instanceof GString ? request.toString() : request)
        params.request = requestToSet
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
            def <T> T ofType(Class<T> responseType) {
                return put(responseType).exchange()?.body
            }

            @Override
            public <T> ListenableFuture<T> ofTypeAsync(Class<T> responseType) {
                ListenableFuture<ResponseEntity> future = put(responseType).exchangeAsync()
                return Futures.transform(future, {ResponseEntity response -> response?.body} as Function)
            }
        }
    }

    @Override
    ResponseEntityReceiving aResponseEntity() {
        return new ResponseEntityReceiving() {
            @Override
            public <T> ListenableFuture<ResponseEntity<T>> ofTypeAsync(Class<T> responseType) {
                return put(responseType).exchangeAsync()
            }

            @Override
            def <T> ResponseEntity<T> ofType(Class<T> responseType) {
                return put(responseType).exchange()
            }
        }
    }

    private PutExecuteForResponseTypeRelated put(Class responseType) {
        return new PutExecuteForResponseTypeRelated(params, restOperations, retryExecutor, responseType)
    }

    @Override
    void ignoringResponse() {
        aResponseEntity().ofType(Object)
    }

    ListenableFuture<Void> ignoringResponseAsync() {
        ListenableFuture<ResponseEntity<Object>> future = aResponseEntity().ofTypeAsync(Object)
        return Futures.transform(future, {null} as Function<ResponseEntity<Object>, Void>)
    }
}
