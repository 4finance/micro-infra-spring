package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import org.springframework.http.HttpEntity

/**
 * Interface that allows sending a request from a {@link HttpEntity}
 */
interface HttpEntitySending<T> {
    T httpEntity(HttpEntity httpEntity)

    T withQueryParameters(Map<String, Object> queryParametersMap)
}