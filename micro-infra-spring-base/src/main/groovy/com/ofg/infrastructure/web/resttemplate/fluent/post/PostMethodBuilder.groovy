package com.ofg.infrastructure.web.resttemplate.fluent.post

import com.google.common.base.Function
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.nurkiewicz.asyncretry.RetryExecutor
import com.nurkiewicz.asyncretry.SyncRetryExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.TracingInfo
import com.ofg.infrastructure.web.resttemplate.fluent.UrlUtils
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.ResponseTypeRelatedRequestsExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.*
import groovy.transform.TypeChecked
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

import java.util.concurrent.Callable

import static com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder.EMPTY_HOST
import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders.NO_PREDEFINED_HEADERS
import static org.springframework.http.HttpMethod.POST
/**
 * Implementation of the {@link org.springframework.http.HttpMethod#POST method} fluent API
 */
@TypeChecked
class PostMethodBuilder extends DataUpdateMethodBuilder<RequestHavingPostMethod, UrlParameterizablePostMethod, ResponseReceivingPostMethod> implements
        PostMethod, RequestHavingPostMethod, ResponseReceivingPostMethod,
        UrlParameterizablePostMethod {

    private final BodyContainingWithQueryParameters<ResponseReceivingPostMethod> withQueryParameters

    PostMethodBuilder(Callable<String> host, RestOperations restOperations, PredefinedHttpHeaders predefinedHeaders, RetryExecutor retryExecutor, TracingInfo tracingInfo) {
        super(predefinedHeaders, restOperations, retryExecutor, tracingInfo)
        params.host = host
        withQueryParameters = new BodyContainingWithQueryParameters<ResponseReceivingPostMethod>(this, params)
    }

    PostMethodBuilder(RestOperations restOperations, TracingInfo tracingInfo) {
        this(EMPTY_HOST, restOperations, NO_PREDEFINED_HEADERS, SyncRetryExecutor.INSTANCE, tracingInfo)
    }

    @Override
    protected HttpMethod getHttpMethod() {
        return POST
    }

    @Override
    ResponseReceivingPostMethod withQueryParameters(Map<String, Object> queryParametersMap) {
        params.url = UrlUtils.addQueryParametersToUri((URI) params.url, queryParametersMap)
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
            def <T> T ofType(Class<T> responseType) {
                return post(responseType).exchange()?.body
            }

            @Override
            public <T> ListenableFuture<T> ofTypeAsync(Class<T> responseType) {
                ListenableFuture<ResponseEntity<T>> future = post(responseType).exchangeAsync()
                return Futures.transform(future, {ResponseEntity response -> response?.body} as Function)
            }
        }
    }

    @Override
    ResponseEntityReceiving aResponseEntity() {
        return new ResponseEntityReceiving() {
            @Override
            public <T> ListenableFuture<ResponseEntity<T>> ofTypeAsync(Class<T> responseType) {
                return post(responseType).exchangeAsync()
            }

            @Override
            def <T> ResponseEntity<T> ofType(Class<T> responseType) {
                return post(responseType).exchange()
            }
        }
    }

    private ResponseTypeRelatedRequestsExecutor post(Class responseType) {
        return new ResponseTypeRelatedRequestsExecutor(params, restOperations, retryExecutor, responseType, POST, tracingInfo)
    }

    @Override
    void ignoringResponse() {
        aResponseEntity().ofType(Object)
    }

    @Override
    ListenableFuture<Void> ignoringResponseAsync() {
        ListenableFuture<ResponseEntity<Object>> future = aResponseEntity().ofTypeAsync(Object)
        return Futures.transform(future, {null} as Function<ResponseEntity<Object>, Void>)
    }

    @Override
    QueryParametersSetting<ResponseReceivingPostMethod> withQueryParameters() {
        return withQueryParameters.withQueryParameters()
    }
}
