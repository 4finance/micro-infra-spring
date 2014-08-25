package com.ofg.infrastructure.web.resttemplate.fluent.head
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.Executable
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersHaving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HttpEntitySending
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ResponseIgnoring
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity

interface ResponseReceivingHeadMethod extends HeadersHaving<ResponseReceivingHeadMethod>, Executable<ResponseReceivingHeadMethod>, HttpEntitySending<ResponseReceivingHeadMethod>, ResponseIgnoring {
    
    ResponseEntity aResponseEntity()

    HttpHeaders httpHeaders()
}
