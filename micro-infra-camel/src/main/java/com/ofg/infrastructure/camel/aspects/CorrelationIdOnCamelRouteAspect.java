package com.ofg.infrastructure.camel.aspects;

import java.lang.invoke.MethodHandles;
import java.util.Random;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Tracer;

import com.ofg.infrastructure.camel.CorrelationIdInterceptor;

/**
 * Aspect that adds {@link CorrelationIdInterceptor} to {@link RouteBuilder} for all incoming messages
 * and every message send to any endpoint.
 */
@Aspect
public class CorrelationIdOnCamelRouteAspect {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String ANY = "*";

    private final Processor correlationIdInterceptor;

    public CorrelationIdOnCamelRouteAspect(Random idGenerator, Tracer trace) {
        correlationIdInterceptor = new CorrelationIdInterceptor(idGenerator, trace);
    }

    @Before(value = "execution(* org.apache.camel.builder.RouteBuilder.addRoutesToCamelContext(..))")
    public void configureCorrelationIdInterceptor(JoinPoint joinPoint) {
        final RouteBuilder targetRouteBuilder = (RouteBuilder) joinPoint.getTarget();
        log.debug("Setting correlationId interception on [{}]", targetRouteBuilder.getClass().getName());
        targetRouteBuilder.interceptFrom().process(correlationIdInterceptor);
        targetRouteBuilder.interceptSendToEndpoint(ANY).process(correlationIdInterceptor);
    }
}
