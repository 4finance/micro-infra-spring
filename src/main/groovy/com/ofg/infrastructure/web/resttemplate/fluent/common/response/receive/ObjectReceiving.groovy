package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

interface ObjectReceiving {
    public <T> T ofType(Class<T> responseType)
}