package com.ofg.infrastructure.web.resttemplate.fluent.head

import com.google.common.base.Function
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.netflix.hystrix.HystrixCommand
import com.nurkiewicz.asyncretry.RetryExecutor
import com.nurkiewicz.asyncretry.SyncRetryExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.TracingInfo
import com.ofg.infrastructure.web.resttemplate.fluent.UrlUtils
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.ResponseTypeRelatedRequestsExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.*
import groovy.transform.TypeChecked
import org.springframework.cloud.sleuth.TraceKeys
import org.springframework.cloud.sleuth.Tracer
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

import java.util.concurrent.Callable

import static com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder.EMPTY_HOST
import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders.NO_PREDEFINED_HEADERS
/**
 * Implementation of the {@link org.springframework.http.HttpMethod#HEAD method} fluent API
 */
@TypeChecked
class HeadMethodBuilder implements HeadMethod, UrlParameterizableHeadMethod, ResponseReceivingHeadMethod,
        QueryParametersHaving<ResponseReceivingHeadMethod> {

    private final Map params = [:]
    private final RestOperations restOperations
    private final RetryExecutor retryExecutor
    private final BodylessWithHeaders<ResponseReceivingHeadMethod> withHeaders
    private final BodylessWithQueryParameters<ResponseReceivingHeadMethod> withQueryParameters
    private final TracingInfo tracingInfo

    HeadMethodBuilder(Callable<String> host, RestOperations restOperations, PredefinedHttpHeaders predefinedHeaders, RetryExecutor retryExecutor, TracingInfo tracingInfo) {
        this.restOperations = restOperations
        params.host = host
        withHeaders =  new BodylessWithHeaders<ResponseReceivingHeadMethod>(this, params, predefinedHeaders)
        withQueryParameters = new BodylessWithQueryParameters<ResponseReceivingHeadMethod>(this, params)
        this.retryExecutor = retryExecutor
        this.tracingInfo = tracingInfo
    }

    HeadMethodBuilder(RestOperations restOperations, TracingInfo tracingInfo) {
        this(EMPTY_HOST, restOperations, NO_PREDEFINED_HEADERS, SyncRetryExecutor.INSTANCE, tracingInfo)
    }

    @Override
    ResponseReceivingHeadMethod onUrl(URI url) {
        params.url = url
        return this
    }

    @Override
    ResponseReceivingHeadMethod onUrl(String url) {
        params.url = new URI(url)
        return this
    }

    @Override
    ResponseReceivingHeadMethod withQueryParameters(Map<String, Object> queryParametersMap) {
        params.url = UrlUtils.addQueryParametersToUri((URI) params.url, queryParametersMap)
        return this
    }

    @Override
    ResponseReceivingHeadMethod httpEntity(HttpEntity httpEntity) {
        params.httpEntity = httpEntity
        return this
    }

    @Override
    UrlParameterizableHeadMethod onUrlFromTemplate(String urlTemplate) {
        params.urlTemplate = urlTemplate
        return this
    }

    @Override
    ResponseReceivingHeadMethod withVariables(Object... urlVariables) {
        params.urlVariablesArray = urlVariables
        return this
    }

    @Override
    ResponseReceivingHeadMethod withVariables(Map<String, ?> urlVariables) {
        params.urlVariablesMap = urlVariables
        return this
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
        return Futures.transform(future, {ResponseEntity re -> re?.headers} as Function<ResponseEntity, HttpHeaders>)
    }

    private ResponseTypeRelatedRequestsExecutor<Object> head() {
        return new ResponseTypeRelatedRequestsExecutor(params, restOperations, retryExecutor, Object, HttpMethod.HEAD, tracingInfo)
    }

    @Override
    void ignoringResponse() {
        aResponseEntity()
    }

    ListenableFuture<Void> ignoringResponseAsync() {
        ListenableFuture<ResponseEntity> future = aResponseEntityAsync()
        return Futures.transform(future, {ResponseEntity re -> null} as Function<ResponseEntity, Void>)
    }

    com.ofg.infrastructure.web.resttemplate.fluent.common.request.HttpMethod<ResponseReceivingHeadMethod, UrlParameterizableHeadMethod> withCircuitBreaker(HystrixCommand.Setter setter) {
        params.hystrix = setter
        return this
    }

    @Override
    com.ofg.infrastructure.web.resttemplate.fluent.common.request.HttpMethod<ResponseReceivingHeadMethod, UrlParameterizableHeadMethod> withCircuitBreaker(HystrixCommand.Setter setter, Callable hystrixFallback) {
        params.hystrixFallback = hystrixFallback
        return withCircuitBreaker(setter)
    }

    @Override
    ResponseReceivingHeadMethod andExecuteFor() {
        return withHeaders.andExecuteFor()
    }

    @Override
    HeadersSetting<ResponseReceivingHeadMethod> withHeaders() {
        return withHeaders.withHeaders()
    }

    @Override
    QueryParametersSetting<ResponseReceivingHeadMethod> withQueryParameters() {
        return withQueryParameters.withQueryParameters()
    }
}
