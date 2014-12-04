package com.ofg.infrastructure.web.resttemplate.fluent.get

import com.google.common.base.Function
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.nurkiewicz.asyncretry.RetryExecutor
import com.nurkiewicz.asyncretry.SyncRetryExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.ResponseTypeRelatedRequestsExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.BodyContainingWithHeaders
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersHaving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ObjectReceiving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ResponseEntityReceiving
import groovy.transform.TypeChecked
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders.NO_PREDEFINED_HEADERS

/**
 * Implementation of the {@link org.springframework.http.HttpMethod#GET method} fluent API
 */
@TypeChecked
class GetMethodBuilder implements GetMethod, UrlParameterizableGetMethod, ResponseReceivingGetMethod, HeadersHaving {

    public static final String EMPTY_HOST = ''

    private final Map params = [:]
    private final RestOperations restOperations
    private final RetryExecutor retryExecutor
    @Delegate private final BodyContainingWithHeaders withHeaders

    GetMethodBuilder(RestOperations restOperations) {
        this(EMPTY_HOST, restOperations, NO_PREDEFINED_HEADERS, SyncRetryExecutor.INSTANCE)
    }

    GetMethodBuilder(String host, RestOperations restOperations, PredefinedHttpHeaders predefinedHeaders, RetryExecutor retryExecutor) {
        this.restOperations = restOperations
        params.host = host
        withHeaders = new BodyContainingWithHeaders(this, params, predefinedHeaders)
        this.retryExecutor = retryExecutor
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
            def <T> T ofType(Class<T> responseType) {
                return get(responseType).exchange()?.body
            }

            @Override
            public <T> ListenableFuture<T> ofTypeAsync(Class<T> responseType) {
                ResponseTypeRelatedRequestsExecutor<T> get = get(responseType)
                ListenableFuture<ResponseEntity<T>> future = get.exchangeAsync()
                return Futures.transform(future, { ResponseEntity input ->
                    return input?.body
                } as Function)
            }
        }
    }

    @Override
    ResponseEntityReceiving aResponseEntity() {
        return new ResponseEntityReceiving() {
            @Override
            public <T> ListenableFuture<ResponseEntity<T>> ofTypeAsync(Class<T> responseType) {
                return get(responseType).exchangeAsync()
            }

            @Override
            def <T> ResponseEntity<T> ofType(Class<T> responseType) {
                return get(responseType).exchange()
            }
        }
    }

    private ResponseTypeRelatedRequestsExecutor get(Class responseType) {
        return new ResponseTypeRelatedRequestsExecutor(params, restOperations, retryExecutor, responseType, HttpMethod.GET)
    }

    @Override
    void ignoringResponse() {
        aResponseEntity().ofType(Object)
    }

    @Override
    ListenableFuture<Void> ignoringResponseAsync() {
        ListenableFuture<ResponseEntity<Object>> future = aResponseEntity().ofTypeAsync(Object)
        return Futures.transform(future, {ResponseEntity r -> null as Void} as Function<ResponseEntity, Void>)
    }

}
