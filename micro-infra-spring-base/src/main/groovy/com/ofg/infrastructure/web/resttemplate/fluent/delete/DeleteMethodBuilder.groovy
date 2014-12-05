package com.ofg.infrastructure.web.resttemplate.fluent.delete

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
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders.NO_PREDEFINED_HEADERS

/**
 * Implementation of the {@link org.springframework.http.HttpMethod#DELETE method} fluent API
 */
@TypeChecked
class DeleteMethodBuilder implements DeleteMethod, UrlParameterizableDeleteMethod, ResponseReceivingDeleteMethod, HeadersHaving,
        MethodParamsApplier<ResponseReceivingDeleteMethod, ResponseReceivingDeleteMethod, UrlParameterizableDeleteMethod> {

    public static final String EMPTY_HOST = ''

    private final Map params = [:]
    private final RestOperations restOperations
    private final RetryExecutor retryExecutor
    @Delegate private final BodylessWithHeaders<ResponseReceivingDeleteMethod> withHeaders

    DeleteMethodBuilder(String host, RestOperations restOperations, PredefinedHttpHeaders predefinedHeaders, RetryExecutor retryExecutor) {
        this.restOperations = restOperations
        params.host = host
        withHeaders =  new BodylessWithHeaders<ResponseReceivingDeleteMethod>(this, params, predefinedHeaders)
        this.retryExecutor = retryExecutor
    }

    DeleteMethodBuilder(RestOperations restOperations) {
        this(EMPTY_HOST, restOperations, NO_PREDEFINED_HEADERS, SyncRetryExecutor.INSTANCE)
    }

    @Override
    ResponseEntity aResponseEntity() {
        return delete().exchange()
    }

    @Override
    ListenableFuture<ResponseEntity> aResponseEntityAsync() {
        return delete().exchangeAsync()
    }

    private ResponseTypeRelatedRequestsExecutor<Object> delete() {
        return new ResponseTypeRelatedRequestsExecutor(params, restOperations, retryExecutor, Object, HttpMethod.DELETE)
    }

    @Override
    void ignoringResponse() {
        aResponseEntity()
    }

    @Override
    ListenableFuture<Void> ignoringResponseAsync() {
        ListenableFuture<ResponseEntity> future = aResponseEntityAsync()
        return Futures.transform(future, {null} as Function<ResponseEntity<Object>, Void>)
    }
}
