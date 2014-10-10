package com.ofg.infrastructure.web.exception

import groovy.transform.PackageScope
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
@PackageScope
/**
 * Example of custom controller advice that can be created in application that uses micro-infra-spring.
 * It's expected that this advice overrides the one defined in micro-infra-spring.
 *
 * Note that this class is created only for test and is located in test scope (not packaged into jar)!
 */
class CustomControllerAdvice {

    /**
     * @ExceptionHandler for Exception that sets custom header
     * SAMPLE_HEADER with value SAMPLE_HEADER_VALUE to demonstrate custom logic that can be applied.
     */
    @ExceptionHandler(Exception)
    ResponseEntity<Object> customExceptionHandling(Exception ex) {
        return new ResponseEntity<Object>(customHeader(), HttpStatus.SERVICE_UNAVAILABLE)
    }

    static HttpHeaders customHeader() {
        HttpHeaders headers = new HttpHeaders()
        headers.add("SAMPLE_HEADER", "SAMPLE_HEADER_VALUE")
        return headers
    }
}
