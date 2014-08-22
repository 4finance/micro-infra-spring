package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.Executable

interface ResponseReceiving extends HeadersHaving<ResponseReceiving>, Executable<ResponseReceiving>, ResponseExtracting {

}
