package com.ofg.infrastructure.web.resttemplate.fluent.common.request

/**
 * Interface for HttpMethods that can have requests consisting of a body
 */
interface RequestHaving<T> {

    /**
     * 
     * @param request - body of the request
     */
    T body(Object request)

    /**
     * Use this method if you don't want to provide a body of the request
     */
    T withoutBody()
    
}
