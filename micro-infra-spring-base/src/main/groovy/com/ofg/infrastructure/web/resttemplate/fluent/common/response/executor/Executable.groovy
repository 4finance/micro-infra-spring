package com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor

/**
 * Interface that gives a nice DSL 
 */
interface Executable<T> {
    T andExecuteFor()
}