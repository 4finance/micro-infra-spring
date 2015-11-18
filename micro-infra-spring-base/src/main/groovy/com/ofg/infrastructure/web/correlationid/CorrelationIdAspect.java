package com.ofg.infrastructure.web.correlationid;

import com.ofg.infrastructure.correlationid.CorrelationIdHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Aspect that adds correlation id to
 * <p/>
 * <ul>
 * <li>{@link RestController} annotated classes
 * with public {@link Callable} methods</li>
 * <li>{@link Controller} annotated classes
 * with public {@link Callable} methods</li>
 * <li>explicit {@link RestOperations}.exchange(..) method calls</li>
 * </ul>
 * <p/>
 * For controllers an around aspect is created that wraps the {@link Callable#call()} method execution
 * <p/>
 * For {@link RestOperations} we are wrapping all executions of the
 * <b>exchange</b> methods and we are extracting {@link HttpHeaders} from the passed {@link HttpEntity}.
 * Next we are adding correlation id header {@link CorrelationIdHolder#CORRELATION_ID_HEADER} with
 * the value taken from {@link CorrelationIdHolder}. Finally the method execution proceeds.
 *
 * @see RestController
 * @see Controller
 * @see RestOperations
 * @see CorrelationIdHolder
 */
@Aspect
public class CorrelationIdAspect {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final int HTTP_ENTITY_PARAM_INDEX = 2;

    @Autowired IdGenerator idGenerator;
    @Autowired Trace trace;

    @Pointcut("@target(org.springframework.web.bind.annotation.RestController)")
    private void anyRestControllerAnnotated() {
    }

    @Pointcut("@target(org.springframework.stereotype.Controller)")
    private void anyControllerAnnotated() {
    }

    @Pointcut("execution(public org.springframework.web.context.request.async.WebAsyncTask *(..))")
    private void anyPublicMethodReturningWebAsyncTask() {
    }

    @Pointcut("(anyRestControllerAnnotated() || anyControllerAnnotated()) && anyPublicMethodReturningWebAsyncTask()")
    private void anyControllerOrRestControllerWithPublicWebAsyncTaskMethod() {
    }

    @Around("anyControllerOrRestControllerWithPublicWebAsyncTaskMethod()")
    public Object wrapWebAsyncTaskWithCorrelationId(ProceedingJoinPoint pjp) throws Throwable {
        final WebAsyncTask webAsyncTask = (WebAsyncTask) pjp.proceed();
        Span span = getSpanOrCreateOne();
        log.debug("Wrapping webAsyncTask with correlation id [" + span.getTraceId() + "]");
        try {
            Field callableField = WebAsyncTask.class.getDeclaredField("callable");
            callableField.setAccessible(true);
            callableField.set(webAsyncTask, trace.wrap(webAsyncTask.getCallable()));
        } catch (NoSuchFieldException ex) {
            log.warn("Cannot wrap webAsyncTask with correlation id", ex);
        }
        return webAsyncTask;
    }

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
        try (TraceScope traceScope = trace.continueSpan(span)) {
            return pjp.proceed(newArgs.toArray());
        }
    }

    private Span getSpanOrCreateOne() {
        return TraceContextHolder.isTracing() ?
                    TraceContextHolder.getCurrentSpan() :
                    MilliSpan.builder().begin(System.currentTimeMillis()).traceId(idGenerator.create()).spanId(idGenerator.create()).build();
    }

    @SuppressWarnings("unchecked")
    private HttpEntity createNewHttpEntity(HttpEntity httpEntity, Span span) {
        HttpHeaders newHttpHeaders = new HttpHeaders();
        newHttpHeaders.putAll(httpEntity.getHeaders());
        if (span != null) {
            addHeaderIfPresent(newHttpHeaders, Trace.SPAN_ID_NAME, span.getSpanId());
            addHeaderIfPresent(newHttpHeaders, Trace.TRACE_ID_NAME, span.getTraceId());
            addHeaderIfPresent(newHttpHeaders, Trace.SPAN_NAME_NAME, span.getName());
            addHeaderIfPresent(newHttpHeaders, Trace.PARENT_ID_NAME, getFirst(span.getParents()));
            addHeaderIfPresent(newHttpHeaders, Trace.PROCESS_ID_NAME, span.getProcessId());
        }
        return new HttpEntity(httpEntity.getBody(), newHttpHeaders);
    }

    private void addHeaderIfPresent(HttpHeaders httpHeaders, String headerName, String value) {
        if (StringUtils.hasText(value)) {
            httpHeaders.add(headerName, value);
        }
    }

    private String getFirst(List<String> parents) {
        return parents == null || parents.isEmpty() ? null : parents.get(0);
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
