package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive;

/**
 * Interface for HttpMethods that can have query parameters set on its requests
 */
public interface QueryParametersHaving<T> {
    QueryParametersSetting<T> withQueryParameters();
}
