package com.ofg.infrastructure.web.resttemplate.custom

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.http.ResponseEntity
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.ResponseExtractor

import java.lang.reflect.Type
/**
 * Default implementation of RestTemplate {@see RestTemplate} with custom
 * <ul>
 *  <li>Error handling {@link ResponseRethrowingErrorHandler}</li>
 *  <li>Request factory
 *      <ul>
 *          <li>{@link BufferingClientHttpRequestFactory} - so that we can access request's body several times throughout request's processing</li>
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
@CompileStatic
@Slf4j
class RestTemplate extends org.springframework.web.client.RestTemplate {

    private final int maxLogResponseChars

    RestTemplate() {
        this(0)
    }

    RestTemplate(int maxLogResponseChars) {
        this.maxLogResponseChars = maxLogResponseChars
        errorHandler = new ResponseRethrowingErrorHandler()
        requestFactory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory())
    }

    @Override
    protected <T> ResponseExtractor<ResponseEntity<T>> responseEntityExtractor(Type responseType) {
        final ResponseExtractor<ResponseEntity<T>> delegate = super.responseEntityExtractor(responseType)
        return new LoggingResponseExtractorWrapper(delegate, maxLogResponseChars)
    }
}
