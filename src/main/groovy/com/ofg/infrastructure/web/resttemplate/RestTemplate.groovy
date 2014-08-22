package com.ofg.infrastructure.web.resttemplate
import groovy.transform.TypeChecked
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory

@TypeChecked
class RestTemplate extends org.springframework.web.client.RestTemplate {
    
    RestTemplate() {
        errorHandler = new ResponseRethrowingErrorHandler()
        requestFactory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory())
    }

}
