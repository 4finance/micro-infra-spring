package com.ofg.infrastructure.web.correlationid;

import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.OLD_CORRELATION_ID_HEADER;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.IdGenerator;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

/**
 * Aspect that adds correlation id to
 *
 * @see RestOperations
 */
@Aspect
public class CorrelationIdAspect {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final int HTTP_ENTITY_PARAM_INDEX = 2;

    @Autowired
    IdGenerator idGenerator;

    @Autowired
    Tracer trace;

    @Pointcut("execution(public * org.springframework.web.client.RestOperations.exchange(..))")
    private void anyExchangeRestOperationsMethod() {
    }

    @Around("anyExchangeRestOperationsMethod()")
    public Object wrapWithCorrelationIdForRestOperations(ProceedingJoinPoint pjp) throws Throwable {
        Span span = getSpanOrCreateOne();
        log.debug("Wrapping RestTemplate call with correlation id [" + span.getTraceId() + "]");
        HttpEntity httpEntity = (HttpEntity) pjp.getArgs()[HTTP_ENTITY_PARAM_INDEX];
        HttpEntity newHttpEntity = createNewHttpEntity(httpEntity, span);
        List<Object> newArgs = modifyHttpEntityInMethodArguments(pjp, newHttpEntity);
        return pjp.proceed(newArgs.toArray());
    }

    private Span getSpanOrCreateOne() {
        return trace.isTracing() ?
                trace.getCurrentSpan() :
                trace.createSpan(Thread.currentThread().getName());
    }

    @SuppressWarnings("unchecked")
    private HttpEntity createNewHttpEntity(HttpEntity httpEntity, Span span) {
        HttpHeaders newHttpHeaders = new HttpHeaders();
        newHttpHeaders.putAll(httpEntity.getHeaders());
        if (span != null) {
            addHeaderIfPresent(newHttpHeaders, Span.SPAN_ID_NAME, String.valueOf(span.getSpanId()));
            addHeaderIfPresent(newHttpHeaders, Span.TRACE_ID_NAME, String.valueOf(span.getTraceId()));
            addHeaderIfPresent(newHttpHeaders, OLD_CORRELATION_ID_HEADER, String.valueOf(span.getTraceId()));
            addHeaderIfPresent(newHttpHeaders, Span.SPAN_NAME_NAME, span.getName());
            addHeaderIfPresent(newHttpHeaders, Span.PARENT_ID_NAME, getFirst(span.getParents()));
            addHeaderIfPresent(newHttpHeaders, Span.PROCESS_ID_NAME, span.getProcessId());
        }
        return new HttpEntity(httpEntity.getBody(), newHttpHeaders);
    }

    private void addHeaderIfPresent(HttpHeaders httpHeaders, String headerName, String value) {
        if (StringUtils.hasText(value)) {
            httpHeaders.add(headerName, value);
        }
    }

    private String getFirst(List<Long> parents) {
        return parents == null || parents.isEmpty() ? null : String.valueOf(parents.get(0));
    }

    private List<Object> modifyHttpEntityInMethodArguments(ProceedingJoinPoint pjp, HttpEntity newHttpEntity) {
        List<Object> newArgs = new ArrayList<>();
        for (int i = 0; i < pjp.getArgs().length; i++) {
            Object arg = pjp.getArgs()[i];
            if (i != HTTP_ENTITY_PARAM_INDEX) {
                newArgs.add(i, arg);
            } else {
                newArgs.add(i, newHttpEntity);
            }
        }
        return newArgs;
    }

}
