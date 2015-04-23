package com.ofg.infrastructure.web.resttemplate.fluent.get

import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HttpEntitySending
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ResponseReceiving
/**
 * {@link org.springframework.http.HttpMethod#GET} method allows receiving requests with body what 
 * {@link ResponseReceiving} interface provides.
 */
interface ResponseReceivingGetMethod extends ResponseReceiving<ResponseReceivingGetMethod>, HttpEntitySending<ResponseReceivingGetMethod>{
}
