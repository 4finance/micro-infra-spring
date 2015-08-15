package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive;

import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.Executable;

/**
 * Interface that provides methods to set parameters on HTTP GET requests
 *
 * @param < T > - interface to return via {@link Executable} when you have finished setting headers
 */
public interface ParametersSetting<T> extends Executable<T> {
}
