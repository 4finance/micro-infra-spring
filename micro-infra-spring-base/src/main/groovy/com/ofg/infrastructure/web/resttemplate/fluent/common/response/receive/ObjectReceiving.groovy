package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import com.google.common.util.concurrent.ListenableFuture


/**
 * Interface that defines what is the type of the received response. 
 * It will return an object of provided class.
 */
abstract class ObjectReceiving {
    
    public <T> T ofType(Class<T> responseType) {
        return ofTypeAsync(responseType).get()
    }

    public abstract <T> ListenableFuture<T> ofTypeAsync(Class<T> responseType)
}