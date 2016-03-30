package com.ofg.infrastructure.web.resttemplate.fluent.options

import com.google.common.base.Function
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.netflix.hystrix.HystrixCommand
import com.nurkiewicz.asyncretry.RetryExecutor
import com.nurkiewicz.asyncretry.SyncRetryExecutor
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
import static org.springframework.http.HttpMethod.OPTIONS
/**
 * Implementation of the {@link org.springframework.http.HttpMethod#HEAD method} fluent API
 */
@TypeChecked
class OptionsMethodBuilder implements
        OptionsMethod, UrlParameterizableOptionsMethod, ResponseReceivingOptionsMethod, AllowHeaderReceiving,
        QueryParametersHaving<ResponseReceivingOptionsMethod> {

    private final Map params = [:]
    private final RestOperations restOperations
    private final RetryExecutor retryExecutor
    private final AllowContainingWithHeaders withHeaders
    private final BodylessWithQueryParameters<ResponseReceivingOptionsMethod> withQueryParameters
    private final OptionsAllowHeaderExecutor allowHeaderExecutor
    private final TracingInfo tracingInfo

    OptionsMethodBuilder(Callable<String> host, RestOperations restOperations, PredefinedHttpHeaders predefinedHeaders, RetryExecutor retryExecutor, TracingInfo tracingInfo) {
        this.restOperations = restOperations
        params.host = host
        withHeaders = new AllowContainingWithHeaders(this, params, predefinedHeaders)
        withQueryParameters = new BodylessWithQueryParameters<ResponseReceivingOptionsMethod>(this, params)
        allowHeaderExecutor = new OptionsAllowHeaderExecutor(restOperations, retryExecutor, params, tracingInfo)
        this.retryExecutor = retryExecutor
        this.tracingInfo = tracingInfo
    }

    OptionsMethodBuilder(RestOperations restOperations,  TracingInfo tracingInfo) {
        this(HttpMethodBuilder.EMPTY_HOST, restOperations, NO_PREDEFINED_HEADERS, SyncRetryExecutor.INSTANCE, tracingInfo)
    }

    @Override
    ResponseReceivingOptionsMethod onUrl(URI url) {
        params.url = url
        return this
    }

    @Override
    ResponseReceivingOptionsMethod onUrl(String url) {
        params.url = new URI(url)
        return this
    }

    @Override
    ResponseReceivingOptionsMethod withQueryParameters(Map<String, Object> queryParametersMap) {
        params.url = UrlUtils.addQueryParametersToUri((URI) params.url, queryParametersMap)
        return this
    }

    @Override
    ResponseReceivingOptionsMethod httpEntity(HttpEntity httpEntity) {
        params.httpEntity = httpEntity
        return this
    }

    @Override
    UrlParameterizableOptionsMethod onUrlFromTemplate(String urlTemplate) {
        params.urlTemplate = urlTemplate
        return this
    }

    @Override
    ResponseReceivingOptionsMethod withVariables(Object... urlVariables) {
        params.urlVariablesArray = urlVariables
        return this
    }

    @Override
    ResponseReceivingOptionsMethod withVariables(Map<String, ?> urlVariables) {
        params.urlVariablesMap = urlVariables
        return this
    }

    @Override
    Set<HttpMethod> allow() {
        return allowHeaderExecutor.allow()
    }

    @Override
    ListenableFuture<Set<HttpMethod>> allowAsync() {
        return allowHeaderExecutor.allowAsync()
    }

    @Override
    ObjectReceiving anObject() {
        return new ObjectReceiving() {
            @Override
            def <T> T ofType(Class<T> responseType) {
                return options(responseType).exchange()?.body
            }

            @Override
            public <T> ListenableFuture<T> ofTypeAsync(Class<T> responseType) {
                def future = options(responseType).exchangeAsync()
                return Futures.transform(future, {ResponseEntity response -> response?.body} as Function)
            }
        }
    }

    @Override
    ResponseEntityReceiving aResponseEntity() {
        return new ResponseEntityReceiving() {
            @Override
            public <T> ListenableFuture<ResponseEntity<T>> ofTypeAsync(Class<T> responseType) {
                return options(responseType).exchangeAsync()
            }

            @Override
            def <T> ResponseEntity<T> ofType(Class<T> responseType) {
                return options(responseType).exchange()
            }
        }
    }

    private ResponseTypeRelatedRequestsExecutor options(Class responseType) {
        return new ResponseTypeRelatedRequestsExecutor(params, restOperations, retryExecutor, responseType, OPTIONS, tracingInfo)
    }

    @Override
    void ignoringResponse() {
        aResponseEntity().ofType(Object)
    }

    @Override
    ListenableFuture<Void> ignoringResponseAsync() {
        ListenableFuture<ResponseEntity<Object>> future = aResponseEntity().ofTypeAsync(Object)
        return Futures.transform(future, {null} as Function<ResponseEntity, Void>)
    }

    com.ofg.infrastructure.web.resttemplate.fluent.common.request.HttpMethod<ResponseReceivingOptionsMethod, UrlParameterizableOptionsMethod> withCircuitBreaker(HystrixCommand.Setter setter) {
        params.hystrix = setter
        return this
    }

    @Override
    com.ofg.infrastructure.web.resttemplate.fluent.common.request.HttpMethod<ResponseReceivingOptionsMethod, UrlParameterizableOptionsMethod> withCircuitBreaker(HystrixCommand.Setter setter, Callable hystrixFallback) {
        params.hystrixFallback = hystrixFallback
        return withCircuitBreaker(setter)
    }

    @Override
    ResponseReceivingOptionsMethod andExecuteFor() {
        return withHeaders.andExecuteFor()
    }

    @Override
    HeadersSetting<ResponseReceivingOptionsMethod> withHeaders() {
        return withHeaders.withHeaders()
    }

    @Override
    QueryParametersSetting<ResponseReceivingOptionsMethod> withQueryParameters() {
        return withQueryParameters.withQueryParameters()
    }
}
