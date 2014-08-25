package com.ofg.infrastructure.web.resttemplate.fluent.delete
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.Executable
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersHaving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HttpEntitySending
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ResponseIgnoring
import org.springframework.http.ResponseEntity

interface ResponseReceivingDeleteMethod extends HeadersHaving<ResponseReceivingDeleteMethod>, Executable<ResponseReceivingDeleteMethod>, HttpEntitySending<ResponseReceivingDeleteMethod>, ResponseIgnoring {

    ResponseEntity aResponseEntity()
    
}
