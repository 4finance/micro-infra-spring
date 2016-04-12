package com.ofg.infrastructure.web.resttemplate.fluent.options

import com.google.common.base.Function
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.nurkiewicz.asyncretry.RetryExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.TracingInfo
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.RestExecutor
import groovy.transform.PackageScope
import groovy.transform.TypeChecked
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

import static org.springframework.http.HttpMethod.OPTIONS
/**
 * Class that executes {@link RestOperations}.exchange() and from {@link org.springframework.http.ResponseEntity#headers}
 * it returns {@link org.springframework.http.HttpHeaders#ALLOW} header
 */
@TypeChecked 
@PackageScope 
class OptionsAllowHeaderExecutor implements AllowHeaderReceiving {

    private final Map params
    private final RestExecutor restExecutor

    OptionsAllowHeaderExecutor(RestOperations restOperations, RetryExecutor retryExecutor, Map params, TracingInfo tracingInfo) {
        this.params = params
        this.restExecutor = new RestExecutor<>(restOperations, retryExecutor, tracingInfo)
    }

    @Override
    ListenableFuture<Set<HttpMethod>> allowAsync() {
        ListenableFuture<ResponseEntity> future = restExecutor.exchangeAsync(OPTIONS, params, Object)
        return Futures.transform(future, {ResponseEntity entity -> extractAllow(entity)} as Function)
    }

    @Override
    Set<HttpMethod> allow() {
        return extractAllow(restExecutor.exchange(OPTIONS, params, Object))
    }

    private Set<HttpMethod> extractAllow(ResponseEntity entity) {
        return entity.headers.getAllow()
    }
}
