package com.ofg.infrastructure.web.correlationid;

import com.ofg.infrastructure.correlationid.CorrelationIdHolder;
import com.ofg.infrastructure.correlationid.CorrelationIdUpdater;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Trace;
import org.springframework.cloud.sleuth.TraceContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER;

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
 * in {@link CorrelationIdUpdater#wrapCallableWithId(Callable)}
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
        log.debug("Wrapping webAsyncTask with correlation id [" + TraceContextHolder.getCurrentSpan().getTraceId() + "]");
        try {
            Field callableField = WebAsyncTask.class.getDeclaredField("callable");
            callableField.setAccessible(true);
            callableField.set(webAsyncTask, CorrelationIdUpdater.wrapCallableWithId(webAsyncTask.getCallable()));
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
        Span span = TraceContextHolder.getCurrentSpan();
        log.debug("Wrapping RestTemplate call with correlation id [" + span.getTraceId() + "]");
        HttpEntity httpEntity = (HttpEntity) pjp.getArgs()[HTTP_ENTITY_PARAM_INDEX];
        HttpEntity newHttpEntity = createNewHttpEntity(httpEntity, span);
        List<Object> newArgs = modifyHttpEntityInMethodArguments(pjp, newHttpEntity);
        return pjp.proceed(newArgs.toArray());
    }

    @SuppressWarnings("unchecked")
    private HttpEntity createNewHttpEntity(HttpEntity httpEntity, Span span) {
        HttpHeaders newHttpHeaders = new HttpHeaders();
        newHttpHeaders.putAll(httpEntity.getHeaders());
        if (span != null) {
            newHttpHeaders.add(CORRELATION_ID_HEADER, span.getTraceId());
            newHttpHeaders.add(Trace.SPAN_ID_NAME, span.getSpanId());
            newHttpHeaders.add(Trace.TRACE_ID_NAME, span.getTraceId());
            newHttpHeaders.add(Trace.NOT_SAMPLED_NAME, span.getName());
            newHttpHeaders.add(Trace.PARENT_ID_NAME, getFirst(span.getParents()));
            newHttpHeaders.add(Trace.PROCESS_ID_NAME, span.getProcessId());
        }
        return new HttpEntity(httpEntity.getBody(), newHttpHeaders);
    }

    private static String getFirst(List<String> parents) {
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
