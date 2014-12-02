package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import com.google.common.util.concurrent.ListenableFuture
import org.springframework.http.ResponseEntity

/**
 * Interface that defines what is the type of the received response. 
 * It will return a {@link ResponseEntity} of the provided class.
 */
interface ResponseEntityReceiving {
    public <T> ListenableFuture<ResponseEntity<T>> ofTypeAsync(Class<T> responseType)
    public <T> ResponseEntity<T> ofType(Class<T> responseType)
}