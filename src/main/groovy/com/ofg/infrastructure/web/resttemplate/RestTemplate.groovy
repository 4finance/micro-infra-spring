package com.ofg.infrastructure.web.resttemplate

import groovy.transform.TypeChecked
import org.springframework.http.HttpMethod
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.HttpMessageConverterExtractor
import org.springframework.web.client.RequestCallback
import org.springframework.web.client.RestClientException

@TypeChecked
class RestTemplate extends org.springframework.web.client.RestTemplate {

    RestTemplate() {
        errorHandler = new ResponseRethrowingErrorHandler()
        requestFactory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory())
    }

    public <T> T putForObject(String url, Object request, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request, responseType)
        HttpMessageConverterExtractor<T> responseExtractor =
                new HttpMessageConverterExtractor<T>(responseType, getMessageConverters())
        return execute(url, HttpMethod.PUT, requestCallback, responseExtractor)
    }
}
