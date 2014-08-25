package com.ofg.infrastructure.web.resttemplate.fluent.post

import com.ofg.infrastructure.web.resttemplate.fluent.common.request.RequestHaving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HttpEntitySending

interface RequestHavingPostMethod extends RequestHaving<ResponseReceivingPostMethod>, HttpEntitySending<ResponseReceivingPostMethod> {

}
