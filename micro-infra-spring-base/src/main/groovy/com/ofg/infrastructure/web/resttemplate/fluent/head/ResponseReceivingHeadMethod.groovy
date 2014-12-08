package com.ofg.infrastructure.web.resttemplate.fluent.head

import com.google.common.util.concurrent.ListenableFuture
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.Executable
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersHaving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HttpEntitySending
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ResponseIgnoring
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity

/**
 * {@link org.springframework.http.HttpMethod#HEAD} method doesn't have a response
 * so this interface provides a {@link ResponseEntity} result or {@link HttpHeaders} to get headers
 */
interface ResponseReceivingHeadMethod extends
        HeadersHaving<ResponseReceivingHeadMethod>, Executable<ResponseReceivingHeadMethod>,
        HttpEntitySending<ResponseReceivingHeadMethod>, ResponseIgnoring {
    
    ResponseEntity aResponseEntity()
    ListenableFuture<ResponseEntity> aResponseEntityAsync()

    HttpHeaders httpHeaders()
    ListenableFuture<HttpHeaders> httpHeadersAsync()
}
