package com.ofg.infrastructure.web.resttemplate.fluent.head
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.Executable
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersHaving
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity

interface ResponseReceivingHeadMethod extends HeadersHaving<ResponseReceivingHeadMethod>, Executable<ResponseReceivingHeadMethod> {

    ResponseEntity aResponseEntity()

    HttpHeaders httpHeaders()
}
