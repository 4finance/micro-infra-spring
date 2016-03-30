package com.ofg.infrastructure.web.resttemplate.fluent.get

import com.google.common.base.Function
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.netflix.hystrix.HystrixCommand
import com.nurkiewicz.asyncretry.RetryExecutor
import com.nurkiewicz.asyncretry.SyncRetryExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.AbstractMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.TracingInfo
import com.ofg.infrastructure.web.resttemplate.fluent.UrlUtils
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.ResponseTypeRelatedRequestsExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.*
import groovy.transform.TypeChecked
import org.springframework.cloud.sleuth.TraceKeys
import org.springframework.cloud.sleuth.Tracer
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

import java.util.concurrent.Callable

import static com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder.EMPTY_HOST
import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders.NO_PREDEFINED_HEADERS
/**
 * Implementation of the {@link org.springframework.http.HttpMethod#GET method} fluent API
 */
@TypeChecked
class GetMethodBuilder extends AbstractMethodBuilder implements GetMethod, UrlParameterizableGetMethod, ResponseReceivingGetMethod,
        HeadersHaving<ResponseReceivingGetMethod>,
        QueryParametersHaving<ResponseReceivingGetMethod> {

    private final RestOperations restOperations
    private final RetryExecutor retryExecutor
    private final BodyContainingWithHeaders<ResponseReceivingGetMethod> withHeaders
    private final BodyContainingWithQueryParameters<ResponseReceivingGetMethod> withQueryParameters
    private final TracingInfo tracingInfo

    GetMethodBuilder(RestOperations restOperations, TracingInfo tracingInfo) {
        this(EMPTY_HOST, restOperations, NO_PREDEFINED_HEADERS, SyncRetryExecutor.INSTANCE, tracingInfo)
    }

    GetMethodBuilder(Callable<String> host, RestOperations restOperations, PredefinedHttpHeaders predefinedHeaders, RetryExecutor retryExecutor, TracingInfo tracingInfo) {
        this.restOperations = restOperations
        params.host = host
        withHeaders = new BodyContainingWithHeaders<ResponseReceivingGetMethod>(this, params, predefinedHeaders)
        withQueryParameters = new BodyContainingWithQueryParameters<ResponseReceivingGetMethod>(this, params)
        this.retryExecutor = retryExecutor
        this.tracingInfo = tracingInfo
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
    ResponseReceivingGetMethod withQueryParameters(Map<String, Object> queryParametersMap) {
        params.url = UrlUtils.addQueryParametersToUri((URI) params.url, queryParametersMap)
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
        if (templateStartsWithPlaceholder()) {
            replaceFirstPlaceholderWithValue()
        }
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
        return new ResponseTypeRelatedRequestsExecutor(params, restOperations, retryExecutor, responseType, HttpMethod.GET, tracingInfo)
    }

    @Override
    void ignoringResponse() {
        aResponseEntity().ofType(Object)
    }

    @Override
    ListenableFuture<Void> ignoringResponseAsync() {
        ListenableFuture<ResponseEntity<Object>> future = aResponseEntity().ofTypeAsync(Object)
        return Futures.transform(future, { ResponseEntity r -> null as Void } as Function<ResponseEntity, Void>)
    }

    com.ofg.infrastructure.web.resttemplate.fluent.common.request.HttpMethod<ResponseReceivingGetMethod, UrlParameterizableGetMethod> withCircuitBreaker(HystrixCommand.Setter setter) {
        params.hystrix = setter
        return this
    }

    @Override
    com.ofg.infrastructure.web.resttemplate.fluent.common.request.HttpMethod<ResponseReceivingGetMethod, UrlParameterizableGetMethod> withCircuitBreaker(HystrixCommand.Setter setter, Callable hystrixFallback) {
        params.hystrixFallback = hystrixFallback
        return withCircuitBreaker(setter)
    }


    @Override
    HeadersSetting<ResponseReceivingGetMethod> withHeaders() {
        return withHeaders.withHeaders()
    }

    @Override
    QueryParametersSetting<ResponseReceivingGetMethod> withQueryParameters() {
        return withQueryParameters.withQueryParameters()
    }

    @Override
    ResponseReceivingGetMethod andExecuteFor() {
        return withHeaders.andExecuteFor()
    }
}
