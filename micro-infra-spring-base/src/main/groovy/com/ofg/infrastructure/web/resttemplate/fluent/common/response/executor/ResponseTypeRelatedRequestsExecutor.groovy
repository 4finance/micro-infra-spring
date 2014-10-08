package com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor

import groovy.transform.TypeChecked
import org.springframework.http.HttpMethod as SpringHttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.HttpEntityUtils.getHttpEntityFrom
import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.UrlParsingUtils.appendPathToHost

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
abstract class ResponseTypeRelatedRequestsExecutor<T> {

    protected final RestOperations restOperations
    protected final Map params
    protected final Class<T> responseType

    ResponseTypeRelatedRequestsExecutor(Map params, RestOperations restOperations, Class<T> responseType) {
        this.restOperations = restOperations
        this.params = params
        this.responseType = responseType
    }

    protected abstract SpringHttpMethod getHttpMethod()

    ResponseEntity<T> exchange() {
        if (params.url) {  
            return restOperations.exchange(
                    new URI(appendPathToHost(params.host as String, params.url as URI)),
                    httpMethod,
                    getHttpEntityFrom(params),
                    responseType)
        } else if (params.urlTemplate) {
            return restOperations.exchange(
                    appendPathToHost(params.host as String, params.urlTemplate as String),
                    httpMethod,
                    getHttpEntityFrom(params),
                    responseType,
                    params.urlVariablesArray as Object[] ?: params.urlVariablesMap as Map<String, ?>)
        }
        throw new InvalidHttpMethodParametersException(params)
    }
    
}

