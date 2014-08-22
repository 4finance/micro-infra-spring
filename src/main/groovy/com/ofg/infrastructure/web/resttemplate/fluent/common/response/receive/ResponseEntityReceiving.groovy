package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import org.springframework.http.ResponseEntity

interface ResponseEntityReceiving {
    public <T> ResponseEntity<T> ofType(Class<T> responseType)
}