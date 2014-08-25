package com.ofg.infrastructure.web.resttemplate.fluent.options
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.Executable
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersHaving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HttpEntitySending
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ResponseExtracting

interface ResponseReceivingOptionsMethod extends HeadersHaving<ResponseReceivingOptionsMethod>, ResponseExtracting, AllowHeaderReceiving, Executable<ResponseReceivingOptionsMethod>, HttpEntitySending<ResponseReceivingOptionsMethod> {

}
