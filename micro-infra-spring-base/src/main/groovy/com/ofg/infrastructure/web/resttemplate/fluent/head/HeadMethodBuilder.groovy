package com.ofg.infrastructure.web.resttemplate.fluent.head

import com.google.common.base.Function
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.nurkiewicz.asyncretry.RetryExecutor
import com.nurkiewicz.asyncretry.SyncRetryExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.ResponseTypeRelatedRequestsExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.BodylessWithHeaders
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersHaving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.MethodParamsApplier
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders
import groovy.transform.TypeChecked
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders.NO_PREDEFINED_HEADERS

/**
 * Implementation of the {@link org.springframework.http.HttpMethod#HEAD method} fluent API
 */
@TypeChecked
class HeadMethodBuilder implements HeadMethod, UrlParameterizableHeadMethod, ResponseReceivingHeadMethod, HeadersHaving,
        MethodParamsApplier<ResponseReceivingHeadMethod, ResponseReceivingHeadMethod, UrlParameterizableHeadMethod> {

    public static final String EMPTY_HOST = ''

    private final Map params = [:]
    private final RestOperations restOperations
    private final RetryExecutor retryExecutor
    @Delegate
    private final BodylessWithHeaders<ResponseReceivingHeadMethod> withHeaders

    HeadMethodBuilder(String host, RestOperations restOperations, PredefinedHttpHeaders predefinedHeaders, RetryExecutor retryExecutor) {
        this.restOperations = restOperations
        params.host = host
        withHeaders = new BodylessWithHeaders<ResponseReceivingHeadMethod>(this, params, predefinedHeaders)
        this.retryExecutor = retryExecutor
    }

    HeadMethodBuilder(RestOperations restOperations) {
        this(EMPTY_HOST, restOperations, NO_PREDEFINED_HEADERS, SyncRetryExecutor.INSTANCE)
    }

    @Override
    ResponseEntity aResponseEntity() {
        return head().exchange()
    }

    @Override
    ListenableFuture<ResponseEntity> aResponseEntityAsync() {
        return head().exchangeAsync()
    }

    @Override
    HttpHeaders httpHeaders() {
        return head().exchange()?.headers
    }

    @Override
    ListenableFuture<HttpHeaders> httpHeadersAsync() {
        ListenableFuture<ResponseEntity> future = aResponseEntityAsync()
        return Futures.transform(future, { ResponseEntity re -> re?.headers } as Function<ResponseEntity, HttpHeaders>)
    }

    private ResponseTypeRelatedRequestsExecutor<Object> head() {
        return new ResponseTypeRelatedRequestsExecutor(params, restOperations, retryExecutor, Object, HttpMethod.HEAD)
    }

    @Override
    void ignoringResponse() {
        aResponseEntity()
    }

    ListenableFuture<Void> ignoringResponseAsync() {
        ListenableFuture<ResponseEntity> future = aResponseEntityAsync()
        return Futures.transform(future, { null } as Function<ResponseEntity<Object>, Void>)
    }
}
