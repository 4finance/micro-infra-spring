package com.ofg.infrastructure.web.resttemplate.fluent.options

import com.google.common.util.concurrent.ListenableFuture
import org.springframework.http.HttpMethod

/**
 * {@link HttpMethod#HEAD} is closely related to retrieving the {@link org.springframework.http.HttpHeaders#ALLOW}
 * request header. This interface provides an easy way to retrieve it.
 */
interface AllowHeaderReceiving {

    /**
     * 
     * @return - a set of values from the {@link org.springframework.http.HttpHeaders#ALLOW} header
     */
    Set<HttpMethod> allow()

    ListenableFuture<Set<HttpMethod>> allowAsync()

}