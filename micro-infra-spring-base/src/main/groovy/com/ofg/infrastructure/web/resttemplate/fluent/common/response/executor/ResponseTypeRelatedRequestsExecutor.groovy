package com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor

import com.google.common.util.concurrent.ListenableFuture
import com.nurkiewicz.asyncretry.RetryExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.TracingInfo
import groovy.transform.TypeChecked
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations
/**
 * Abstraction over {@link RestOperations} that for a {@link ResponseTypeRelatedRequestsExecutor#getHttpMethod()} 
 * checks whether user passed an URL or a template. Basing on this we create an execute a request.
 * 
 * The parameters passed to the <b>exchange</b> method are set based on whether user wants to call
 * an external or internal service and whether he wants to call a direct URL or wants to do it from a template.
 * If the user passes relative path (i.e. ws/api and wants to call a service http://4finance.net) the abstraction
 * via {@link UrlParsingUtils} will prepend a slash to the URL thus it will call http://4finance.net/ws/api. 
 * For more details check {@link UrlParsingUtils}
 * 
 * @param < T >
 *     
 * @see UrlParsingUtils
 * @see RestOperations
 */
@TypeChecked
public class ResponseTypeRelatedRequestsExecutor<T> {

    protected final RestExecutor<T> restExecutor
    protected final Map params
    private final Class<T> responseType
    final HttpMethod httpMethod

    public ResponseTypeRelatedRequestsExecutor(Map params, RestOperations restOperations, RetryExecutor retryExecutor, Class<T> responseType, HttpMethod httpMethod, TracingInfo tracingInfo) {
        this.params = params
        this.responseType = responseType
        this.restExecutor = new RestExecutor(restOperations, retryExecutor, tracingInfo)
        this.httpMethod = httpMethod
        params.url = params?.urlWithQueryParameters?params.urlWithQueryParameters:params.url
    }

    ResponseEntity<T> exchange() {
        return restExecutor.exchange(httpMethod, params, responseType)
    }

    ListenableFuture<ResponseEntity<T>> exchangeAsync() {
        return restExecutor.exchangeAsync(httpMethod, params, responseType)
    }

}

