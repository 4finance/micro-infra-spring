package com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor

import com.google.common.base.Function
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.nurkiewicz.asyncretry.RetryExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.TracingInfo
import groovy.transform.TypeChecked
import org.springframework.cloud.sleuth.TraceKeys
import org.springframework.cloud.sleuth.Tracer
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations
/**
 * Class that executes {@link RestOperations}.exchange() and from {@link org.springframework.http.ResponseEntity#headers}
 * it returns {@link org.springframework.http.HttpHeaders#LOCATION} header
 */
@TypeChecked
abstract class LocationFindingExecutor implements LocationReceiving {

    protected final Map params = [:]
    protected final RestOperations restOperations
    protected final RetryExecutor retryExecutor
    private final RestExecutor restExecutor

    LocationFindingExecutor(RestOperations restOperations, RetryExecutor retryExecutor, TracingInfo tracingInfo) {
        this.restOperations = restOperations
        this.retryExecutor = retryExecutor
        this.restExecutor = new RestExecutor<>(restOperations, retryExecutor, tracingInfo)
    }

    protected abstract HttpMethod getHttpMethod()

    @Override
    URI forLocation() {
        return getLocation(restExecutor.exchange(httpMethod, params, params.request.class))
    }

    @Override
    ListenableFuture<URI> forLocationAsync() {
        ListenableFuture<ResponseEntity> future = restExecutor.exchangeAsync(httpMethod, params, params.request.class)
        return Futures.transform(future, {ResponseEntity entity -> getLocation(entity)} as Function)
    }

    private static URI getLocation(HttpEntity entity) {
        return entity?.headers?.getLocation()
    }
}
