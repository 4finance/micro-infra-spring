package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

/**
 * Interface allowing to retrieve a response in a form of an object or 
 * a {@link org.springframework.http.ResponseEntity}. You can also choose
 * to ignore the received response.
 * 
 * @see ObjectReceiving
 * @see ResponseEntityReceiving
 * @see ResponseIgnoring
 */
interface ResponseExtracting extends ResponseIgnoring {

    ObjectReceiving anObject()

    ResponseEntityReceiving aResponseEntity()
    
}