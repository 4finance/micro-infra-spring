package com.ofg.infrastructure.web.correlationid;

import com.ofg.infrastructure.correlationid.CorrelationIdHolder;
import com.ofg.infrastructure.correlationid.CorrelationIdUpdater;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @see CorrelationIdFilter
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

    @Pointcut("execution(public java.util.concurrent.Callable *(..))")
    private void anyPublicMethodReturningCallable() {
    }

    @Pointcut("execution(public org.springframework.web.context.request.async.WebAsyncTask *(..))")
    private void anyPublicMethodReturningWebAsyncTask() {
    }

    @Pointcut("(anyRestControllerAnnotated() || anyControllerAnnotated()) && anyPublicMethodReturningCallable()")
    private void anyControllerOrRestControllerWithPublicCallableMethod() {
    }

    @Pointcut("(anyRestControllerAnnotated() || anyControllerAnnotated()) && anyPublicMethodReturningWebAsyncTask()")
    private void anyControllerOrRestControllerWithPublicWebAsyncTaskMethod() {
    }

    @Around("anyControllerOrRestControllerWithPublicCallableMethod()")
    public Object wrapCallableWithCorrelationId(ProceedingJoinPoint pjp) throws Throwable {
        final Callable callable = (Callable) pjp.proceed();
        log.debug("Wrapping callable with correlation id [" + CorrelationIdHolder.get() + "]");
        return CorrelationIdUpdater.wrapCallableWithId(new Callable() {
            @Override
            public Object call() throws Exception {
                return callable.call();
            }
        });
    }

    @Around("anyControllerOrRestControllerWithPublicWebAsyncTaskMethod()")
    public Object wrapWebAsyncTaskWithCorrelationId(ProceedingJoinPoint pjp) throws Throwable {
        final WebAsyncTask webAsyncTask = (WebAsyncTask) pjp.proceed();
        log.debug("Wrapping webAsyncTask with correlation id [" + CorrelationIdHolder.get() + "]");
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
        String correlationId = CorrelationIdHolder.get();
        log.debug("Wrapping RestTemplate call with correlation id [" + correlationId + "]");
        HttpEntity httpEntity = (HttpEntity) pjp.getArgs()[HTTP_ENTITY_PARAM_INDEX];
        HttpEntity newHttpEntity = createNewHttpEntity(httpEntity, correlationId);
        List<Object> newArgs = modifyHttpEntityInMethodArguments(pjp, newHttpEntity);
        return pjp.proceed(newArgs.toArray());
    }

    @SuppressWarnings("unchecked")
    private HttpEntity createNewHttpEntity(HttpEntity httpEntity, String correlationId) {
        HttpHeaders newHttpHeaders = new HttpHeaders();
        newHttpHeaders.putAll(httpEntity.getHeaders());
        newHttpHeaders.add(CORRELATION_ID_HEADER, correlationId);
        return new HttpEntity(httpEntity.getBody(), newHttpHeaders);
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
