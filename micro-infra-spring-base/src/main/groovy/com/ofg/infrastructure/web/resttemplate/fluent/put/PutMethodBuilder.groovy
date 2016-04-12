package com.ofg.infrastructure.web.resttemplate.fluent.put

import com.google.common.base.Function
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.nurkiewicz.asyncretry.RetryExecutor
import com.nurkiewicz.asyncretry.SyncRetryExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.TracingInfo
import com.ofg.infrastructure.web.resttemplate.fluent.UrlUtils
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.ResponseTypeRelatedRequestsExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.*
import com.ofg.infrastructure.web.resttemplate.fluent.post.DataUpdateMethodBuilder
import groovy.transform.TypeChecked
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

import java.util.concurrent.Callable

import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders.NO_PREDEFINED_HEADERS
import static org.springframework.http.HttpMethod.PUT
/**
 * Implementation of the {@link org.springframework.http.HttpMethod#PUT method} fluent API
 */
@TypeChecked
class PutMethodBuilder extends DataUpdateMethodBuilder<RequestHavingPutMethod, UrlParameterizablePutMethod, ResponseReceivingPutMethod> implements
        PutMethod, RequestHavingPutMethod, ResponseReceivingPutMethod,
        UrlParameterizablePutMethod {

    private final WithQueryParameters<ResponseReceivingPutMethod> withQueryParameters

    PutMethodBuilder(Callable<String> host, RestOperations restOperations, PredefinedHttpHeaders predefinedHeaders, RetryExecutor retryExecutor, TracingInfo tracingInfo) {
        super(predefinedHeaders, restOperations, retryExecutor, tracingInfo)
        params.host = host
        withQueryParameters = new BodyContainingWithQueryParameters<ResponseReceivingPutMethod>(this, params)
    }

    @Override
    ResponseReceivingPutMethod withQueryParameters(Map<String, Object> queryParametersMap) {
        params.url = UrlUtils.addQueryParametersToUri((URI) params.url, queryParametersMap)
        return this
    }

    PutMethodBuilder(RestOperations restOperations, TracingInfo tracingInfo) {
        this(HttpMethodBuilder.EMPTY_HOST, restOperations, NO_PREDEFINED_HEADERS, SyncRetryExecutor.INSTANCE, tracingInfo)
    }

    @Override
    protected HttpMethod getHttpMethod() {
        return PUT
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
                return Futures.transform(future, { ResponseEntity response -> response?.body } as Function)
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

    private ResponseTypeRelatedRequestsExecutor put(Class responseType) {
        return new ResponseTypeRelatedRequestsExecutor(params, restOperations, retryExecutor, responseType, PUT, tracingInfo)
    }

    @Override
    void ignoringResponse() {
        aResponseEntity().ofType(Object)
    }

    ListenableFuture<Void> ignoringResponseAsync() {
        ListenableFuture<ResponseEntity<Object>> future = aResponseEntity().ofTypeAsync(Object)
        return Futures.transform(future, { null } as Function<ResponseEntity<Object>, Void>)
    }

    @Override
    QueryParametersSetting<ResponseReceivingPutMethod> withQueryParameters() {
        return withQueryParameters.withQueryParameters()
    }
}
