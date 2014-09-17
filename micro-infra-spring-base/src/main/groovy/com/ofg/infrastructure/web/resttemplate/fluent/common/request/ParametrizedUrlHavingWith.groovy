package com.ofg.infrastructure.web.resttemplate.fluent.common.request

/**
 * Interface for urls that consist of variables
 */
interface ParametrizedUrlHavingWith<T> {

    /**
     * 
     * @param urlVariables - variables passed as an array
     */
    T withVariables(Object... urlVariables)

    /**
     * 
     * @param urlVariables - variables passed as a key, 
     * value pair where key is the param name and value is its value
     */
    T withVariables(Map<String, ?> urlVariables)

}
