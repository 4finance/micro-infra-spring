package com.ofg.infrastructure.web.resttemplate.fluent.post
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.LocationReceiving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ResponseReceiving

/**
 * The {@link org.springframework.http.HttpMethod#POST} HTTP method can send a message with a body 
 * and can post for location. 
 *
 * @see ResponseReceiving
 * @see LocationReceiving
 */
interface ResponseReceivingPostMethod extends ResponseReceiving<ResponseReceivingPostMethod>, LocationReceiving {
    
}
