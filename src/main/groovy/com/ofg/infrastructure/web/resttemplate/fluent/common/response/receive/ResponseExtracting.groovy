package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

interface ResponseExtracting extends ResponseIgnoring {

    ObjectReceiving anObject()

    ResponseEntityReceiving aResponseEntity()
    
}