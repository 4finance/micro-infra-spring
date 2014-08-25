package com.ofg.infrastructure.web.resttemplate.fluent.put
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.LocationReceiving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HttpEntitySending
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ResponseReceiving

interface ResponseReceivingPutMethod extends ResponseReceiving, LocationReceiving, HttpEntitySending<ResponseReceivingPutMethod> {
    
}
