package com.ofg.infrastructure.web.resttemplate.fluent.common.request

interface ParametrizedUrlHavingWith<T> {

    T withVariables(Object... urlVariables)
    T withVariables(Map<String, ?> urlVariables)

}
