package com.ofg.infrastructure

import groovy.transform.CompileStatic

/**
 * Some of the components created by the micro-infra-spring library are instances of classes widely used in
 * Spring community and it's possible that application using the library might create its own bean of some class
 * causing NoUniqueBeanDefinitionException. Good example is RestOperations.
 * Spring @Qualifier is a solution for the problem.
 * This class is used to hold value for library specific value passed as argument to @Qualifier.
 */
@CompileStatic
class MicroInfraSpringQualifier {

    /**
     * Library specific value for @Qualifier annotation
     */
    public static final String VALUE = "micro-infra-spring"

    private MicroInfraSpringQualifier() {
        throw new UnsupportedOperationException("Class should not be instantiated!")
    }
}
