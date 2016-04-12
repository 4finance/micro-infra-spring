package com.ofg.infrastructure.web.resttemplate.fluent.delete

import com.google.common.base.Function
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.netflix.hystrix.HystrixCommand
import com.nurkiewicz.asyncretry.RetryExecutor
import com.nurkiewicz.asyncretry.SyncRetryExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.AbstractMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.TracingInfo
import com.ofg.infrastructure.web.resttemplate.fluent.UrlUtils
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.ResponseTypeRelatedRequestsExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.*
import groovy.transform.TypeChecked
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

import java.util.concurrent.Callable

import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders.NO_PREDEFINED_HEADERS

/**
 * Implementation of the {@link org.springframework.http.HttpMethod#DELETE method} fluent API
 */
@TypeChecked
class  DeleteMethodBuilder extends AbstractMethodBuilder implements DeleteMethod, UrlParameterizableDeleteMethod,
        ResponseReceivingDeleteMethod, QueryParametersHaving<ResponseReceivingDeleteMethod> {


    private final RestOperations restOperations
    private final RetryExecutor retryExecutor
    private final BodylessWithHeaders<ResponseReceivingDeleteMethod> withHeaders
    private final BodylessWithQueryParameters<ResponseReceivingDeleteMethod> withQueryParameters
    private final TracingInfo tracingInfo

    DeleteMethodBuilder(Callable<String> host, RestOperations restOperations, PredefinedHttpHeaders predefinedHeaders, RetryExecutor retryExecutor, TracingInfo tracingInfo) {
        this.restOperations = restOperations
        params.host = host
        withHeaders =  new BodylessWithHeaders<ResponseReceivingDeleteMethod>(this, params, predefinedHeaders)
        withQueryParameters = new BodylessWithQueryParameters<ResponseReceivingDeleteMethod>(this, params)
        this.retryExecutor = retryExecutor
        this.tracingInfo = tracingInfo
    }

    DeleteMethodBuilder(RestOperations restOperations, TracingInfo tracingInfo) {
        this(HttpMethodBuilder.EMPTY_HOST, restOperations, NO_PREDEFINED_HEADERS, SyncRetryExecutor.INSTANCE, tracingInfo)
    }

    @Override
    ResponseReceivingDeleteMethod withQueryParameters(Map<String, Object> queryParametersMap) {
        params.url = UrlUtils.addQueryParametersToUri((URI) params.url, queryParametersMap)
        return this
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
        return new ResponseTypeRelatedRequestsExecutor(params, restOperations, retryExecutor, Object, HttpMethod.DELETE, tracingInfo)
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

    com.ofg.infrastructure.web.resttemplate.fluent.common.request.HttpMethod<ResponseReceivingDeleteMethod, UrlParameterizableDeleteMethod> withCircuitBreaker(HystrixCommand.Setter setter) {
        params.hystrix = setter
        return this
    }

    @Override
    com.ofg.infrastructure.web.resttemplate.fluent.common.request.HttpMethod<ResponseReceivingDeleteMethod, UrlParameterizableDeleteMethod> withCircuitBreaker(HystrixCommand.Setter setter, Callable hystrixFallback) {
        params.hystrixFallback = hystrixFallback
        return withCircuitBreaker(setter)
    }

    @Override
    UrlParameterizableDeleteMethod onUrlFromTemplate(String urlTemplate) {
        params.urlTemplate = urlTemplate
        return this
    }

    @Override
    ResponseReceivingDeleteMethod onUrl(URI url) {
        params.url = url
        return this
    }

    @Override
    ResponseReceivingDeleteMethod onUrl(String url) {
        params.url = new URI(url)
        return this
    }

    @Override
    ResponseReceivingDeleteMethod httpEntity(HttpEntity httpEntity) {
        params.httpEntity = httpEntity
        return this
    }

    ResponseReceivingDeleteMethod withVariables(Object... urlVariables) {
        params.urlVariablesArray = urlVariables
        if(templateStartsWithPlaceholder()) {
            replaceFirstPlaceholderWithValue()
        }
        this
    }

    ResponseReceivingDeleteMethod withVariables(Map<String, ?> urlVariables) {
        params.urlVariablesMap = urlVariables
        this
    }

    @Override
    ResponseReceivingDeleteMethod andExecuteFor() {
        return withHeaders.andExecuteFor()
    }

    @Override
    HeadersSetting<ResponseReceivingDeleteMethod> withHeaders() {
        return withHeaders.withHeaders()
    }

    @Override
    QueryParametersSetting<ResponseReceivingDeleteMethod> withQueryParameters() {
        return withQueryParameters.withQueryParameters()
    }
}
