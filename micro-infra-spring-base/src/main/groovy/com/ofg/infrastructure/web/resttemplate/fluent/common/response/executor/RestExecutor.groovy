package com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor

import com.google.common.util.concurrent.ListenableFuture
import com.nurkiewicz.asyncretry.RetryExecutor
import com.nurkiewicz.asyncretry.SyncRetryExecutor
import com.ofg.infrastructure.correlationid.CorrelationIdHolder
import com.ofg.infrastructure.correlationid.CorrelationIdUpdater
import groovy.transform.TypeChecked
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

import java.util.concurrent.ExecutionException

import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.UrlParsingUtils.appendPathToHost

/**
 * Utility class that extracts {@link HttpEntity} from the provided map of passed parameters
 */
@TypeChecked
final class RestExecutor<T> {
    private final RestOperations restOperations
    private final RetryExecutor retryExecutor

    RestExecutor(RestOperations restOperations, RetryExecutor retryExecutor) {
        this.restOperations = restOperations
        this.retryExecutor = retryExecutor
    }

    ResponseEntity<T> exchange(HttpMethod httpMethod, Map params, Class<T> responseType) {
        try {
            return exchangeInternal(params, httpMethod, responseType).get()
        } catch (ExecutionException e) {
            throw e.cause
        }
    }

    ListenableFuture<ResponseEntity<T>> exchangeAsync(HttpMethod httpMethod, Map params, Class<T> responseType) {
        throwIfAsyncWithoutExecutor()
        return exchangeInternal(params, httpMethod, responseType)
    }

    private ListenableFuture<ResponseEntity<T>> exchangeInternal(Map params, HttpMethod httpMethod, Class<T> responseType) {
        if (params.url) {
            return urlExchange(httpMethod, params, responseType)
        } else if (params.urlTemplate) {
            return urlTemplateExchange(httpMethod, params, responseType)
        }
        throw new InvalidHttpMethodParametersException(params)
    }

    private void throwIfAsyncWithoutExecutor() {
        if (retryExecutor == SyncRetryExecutor.INSTANCE)
            throw new IllegalStateException("Async execution is only enabled with retrying executor. Try .retryUsing(executor.dontRetry()) ")
    }


    private ListenableFuture<ResponseEntity<T>> urlTemplateExchange(HttpMethod httpMethod, Map params, Class<T> responseType) {
        return withRetry {
            restOperations.exchange(
                    appendPathToHost(params.host as String, params.urlTemplate as String),
                    httpMethod,
                    getHttpEntityFrom(params),
                    responseType,
                    params.urlVariablesArray as Object[] ?: params.urlVariablesMap as Map<String, ?>)
        }
    }


    private ListenableFuture<ResponseEntity<T>> urlExchange(HttpMethod httpMethod, Map params, Class<T> responseType) {
        return withRetry {
            return restOperations.exchange(
                    new URI(appendPathToHost(params.host as String, params.url as URI)),
                    httpMethod,
                    getHttpEntityFrom(params),
                    responseType)
        }
    }

    private ListenableFuture<ResponseEntity<T>> withRetry(Closure<ResponseEntity<T>> httpInvocation) {
        String correlationId = CorrelationIdHolder.get()
        return retryExecutor.getWithRetry {
            return CorrelationIdUpdater.withId(correlationId) {
                return httpInvocation.call()
            }
        }
    }

    static HttpEntity<Object> getHttpEntityFrom(Map params) {
        if (params.httpEntity) {
            return params.httpEntity as HttpEntity
        }
        HttpHeaders headers = params.headers as HttpHeaders
        HttpEntity<?> httpEntity = new HttpEntity<Object>(params.request, headers)
        return httpEntity
    }
}
