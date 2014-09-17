package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

interface ResponseIgnoring {
    /**
     * When you do not care about the received response for your HTTP request
     */
    void ignoringResponse()
}