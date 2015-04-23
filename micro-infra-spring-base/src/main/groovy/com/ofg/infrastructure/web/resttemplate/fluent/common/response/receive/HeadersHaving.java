package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive;

/**
 * Interface for HttpMethods that can have headers set on its requests
 */
public interface HeadersHaving<T> {
    HeadersSetting<T> withHeaders();
}
