package com.ofg.infrastructure.web.resttemplate.custom
import groovy.transform.TypeChecked
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory

/**
 * Default implementation of RestTemplate {@see RestTemplate} with custom
 * <ul>
 *  <li>Error handling {@link ResponseRethrowingErrorHandler}</li>
 *  <li>Request factory
 *      <ul>
 *          <li>@link BufferingClientHttpRequestFactory} - so that we can access request's body several times throughout request's processing</li>
 *          <li>with default {@link org.springframework.http.client.ClientHttpRequestFactory} - {@link SimpleClientHttpRequestFactory}</li>
 *      </ul>
 *  </li>
 * </ul>
 *
 * @see RestTemplate
 * @see ResponseRethrowingErrorHandler
 * @see BufferingClientHttpRequestFactory
 * @see org.springframework.http.client.ClientHttpRequestFactory
 */
@TypeChecked
class RestTemplate extends org.springframework.web.client.RestTemplate {
    
    RestTemplate() {
        errorHandler = new ResponseRethrowingErrorHandler()
        requestFactory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory())
    }

}
