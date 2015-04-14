package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.Executable

/**
 * Interface that is a base for all the HttpMethods
 */
interface ResponseReceiving<T> extends HeadersHaving<T>, ResponseExtracting {
    
}
