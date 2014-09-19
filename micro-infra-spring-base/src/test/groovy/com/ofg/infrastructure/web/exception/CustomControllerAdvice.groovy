package com.ofg.infrastructure.web.exception

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class CustomControllerAdvice {

    @ExceptionHandler(Exception)
    ResponseEntity customExceptionHandling(Exception ex) {
        return new ResponseEntity(getRetryAfterHeader(), HttpStatus.SERVICE_UNAVAILABLE)
    }

    static HttpHeaders getRetryAfterHeader() {
        HttpHeaders headers = new HttpHeaders()
        headers.add("Retry-After", "1000")
        return headers
    }
}
