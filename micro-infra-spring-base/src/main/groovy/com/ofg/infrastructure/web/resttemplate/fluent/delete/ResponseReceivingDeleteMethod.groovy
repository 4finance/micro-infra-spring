package com.ofg.infrastructure.web.resttemplate.fluent.delete

import com.google.common.util.concurrent.ListenableFuture
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.Executable
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersHaving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HttpEntitySending
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ResponseIgnoring
import org.springframework.http.ResponseEntity

/**
 * {@link org.springframework.http.HttpMethod#DELETE} method doesn't have a response
 * so this interface provides only a {@link ResponseEntity} result or {@link ResponseIgnoring} one
 */
interface ResponseReceivingDeleteMethod extends
        HeadersHaving<ResponseReceivingDeleteMethod>, Executable<ResponseReceivingDeleteMethod>,
        HttpEntitySending<ResponseReceivingDeleteMethod>, ResponseIgnoring {

    ResponseEntity aResponseEntity()
    ListenableFuture<ResponseEntity> aResponseEntityAsync()

}
