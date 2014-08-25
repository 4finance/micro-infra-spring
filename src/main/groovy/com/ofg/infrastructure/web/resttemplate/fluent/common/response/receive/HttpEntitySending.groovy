package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import org.springframework.http.HttpEntity

interface HttpEntitySending<T> {
    T httpEntity(HttpEntity httpEntity)
}