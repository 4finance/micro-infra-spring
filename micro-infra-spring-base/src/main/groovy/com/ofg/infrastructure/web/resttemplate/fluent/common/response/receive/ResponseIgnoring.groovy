package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import com.google.common.util.concurrent.ListenableFuture

interface ResponseIgnoring {
    /**
     * When you do not care about the received response for your HTTP request
     */
    void ignoringResponse()
    ListenableFuture<Void> ignoringResponseAsync()
}