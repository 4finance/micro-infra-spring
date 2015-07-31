package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive;

import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.Executable;

/**
 * Interface that provides methods to set query parameters on HTTP requests
 *
 * @param < T > - interface to return via {@link Executable} when you have finished setting query parameters
 */
public interface QueryParametersSetting<T> extends Executable<T> {
    QueryParametersSetting<T> parameter(String name, Object value);
}
