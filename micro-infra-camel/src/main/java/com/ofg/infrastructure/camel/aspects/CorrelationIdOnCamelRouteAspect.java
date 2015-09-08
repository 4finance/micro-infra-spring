package com.ofg.infrastructure.camel.aspects;

import com.ofg.infrastructure.camel.CorrelationIdInterceptor;
import com.ofg.infrastructure.correlationid.UuidGenerator;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * Aspect that adds {@link CorrelationIdInterceptor} to {@link RouteBuilder} for all incoming messages
 * and every message send to any endpoint.
 */
@Aspect
public class CorrelationIdOnCamelRouteAspect {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String ANY = "*";

    private final Processor correlationIdInterceptor;

    public CorrelationIdOnCamelRouteAspect(UuidGenerator uuidGenerator) {
        correlationIdInterceptor = new CorrelationIdInterceptor(uuidGenerator);
    }

    @Before(value = "execution(* org.apache.camel.builder.RouteBuilder.addRoutesToCamelContext(..))")
    public void configureCorrelationIdInterceptor(JoinPoint joinPoint) {
        final RouteBuilder targetRouteBuilder = (RouteBuilder) joinPoint.getTarget();
        log.debug("Setting correlationId interception on [{}]", targetRouteBuilder.getClass().getName());
        targetRouteBuilder.interceptFrom().process(correlationIdInterceptor);
        targetRouteBuilder.interceptSendToEndpoint(ANY).process(correlationIdInterceptor);
    }
}
