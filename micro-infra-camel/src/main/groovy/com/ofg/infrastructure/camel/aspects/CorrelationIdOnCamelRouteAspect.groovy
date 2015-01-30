package com.ofg.infrastructure.camel.aspects;

import com.ofg.infrastructure.camel.CorrelationIdInterceptor
import com.ofg.infrastructure.correlationid.UuidGenerator
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect

/**
 * Aspect that adds {@link CorrelationIdInterceptor} to {@link RouteBuilder} for all incoming messages
 * and every message send to any endpoint.
 */
@Slf4j
@Aspect
@CompileStatic
class CorrelationIdOnCamelRouteAspect {

    private static final String ANY = '*'
    private final Processor correlationIdInterceptor

    CorrelationIdOnCamelRouteAspect(UuidGenerator uuidGenerator) {
        correlationIdInterceptor = new CorrelationIdInterceptor(uuidGenerator)
    }

    @Around(value = "execution(* org.apache.camel.builder.RouteBuilder.addRoutesToCamelContext(..))")
    Object aroundAddRoutesNoArgs(ProceedingJoinPoint joinPoint) throws Throwable {
        RouteBuilder targetRouteBuilder = (RouteBuilder)joinPoint.getTarget()
        log.debug("Setting correlationId interception on ${targetRouteBuilder.getClass().getName()}")
        targetRouteBuilder.interceptFrom().process(correlationIdInterceptor)
        targetRouteBuilder.interceptSendToEndpoint(ANY).process(correlationIdInterceptor)
        return joinPoint.proceed()
    }
}
