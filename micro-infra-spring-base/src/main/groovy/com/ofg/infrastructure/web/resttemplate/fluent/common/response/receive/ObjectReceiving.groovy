package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import com.google.common.util.concurrent.ListenableFuture


/**
 * Interface that defines what is the type of the received response. 
 * It will return an object of provided class.
 */
interface ObjectReceiving {
    
    public <T> T ofType(Class<T> responseType)

    public <T> ListenableFuture<T> ofTypeAsync(Class<T> responseType)
}