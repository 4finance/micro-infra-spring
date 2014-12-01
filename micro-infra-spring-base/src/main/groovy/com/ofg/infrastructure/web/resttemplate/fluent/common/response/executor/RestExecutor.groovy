package com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor

import com.google.common.util.concurrent.ListenableFuture
import com.nurkiewicz.asyncretry.RetryExecutor
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

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
        return exchangeAsync(httpMethod, params, responseType).get()
    }

    ListenableFuture<ResponseEntity<T>> exchangeAsync(HttpMethod httpMethod, Map params, Class<T> responseType) {
        if (params.url) {
            return callUrlWithRetry(httpMethod, params, responseType)
        } else if (params.urlTemplate) {
            return callUrlTemplateWithRetry(httpMethod, params, responseType)
        }
        throw new InvalidHttpMethodParametersException(params)
    }

    protected ListenableFuture<ResponseEntity<T>> callUrlTemplateWithRetry(HttpMethod httpMethod, Map params, Class<T> responseType) {
        return retryExecutor.getWithRetry {
            //TODO Correlation ID
            return restOperations.exchange(
                    appendPathToHost(params.host as String, params.urlTemplate as String),
                    httpMethod,
                    getHttpEntityFrom(params),
                    responseType,
                    params.urlVariablesArray as Object[] ?: params.urlVariablesMap as Map<String, ?>)
        }
    }

    protected ListenableFuture<ResponseEntity<T>> callUrlWithRetry(HttpMethod httpMethod, Map params, Class<T> responseType) {
        return retryExecutor.getWithRetry {
            //TODO Correlation ID
            restOperations.exchange(
                    new URI(appendPathToHost(params.host as String, params.url as URI)),
                    httpMethod,
                    getHttpEntityFrom(params),
                    responseType)
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
