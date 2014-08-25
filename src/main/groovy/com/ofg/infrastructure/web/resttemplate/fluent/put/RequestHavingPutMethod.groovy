package com.ofg.infrastructure.web.resttemplate.fluent.put

import com.ofg.infrastructure.web.resttemplate.fluent.common.request.RequestHaving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HttpEntitySending

interface RequestHavingPutMethod extends RequestHaving<ResponseReceivingPutMethod>, HttpEntitySending<ResponseReceivingPutMethod> {

}
