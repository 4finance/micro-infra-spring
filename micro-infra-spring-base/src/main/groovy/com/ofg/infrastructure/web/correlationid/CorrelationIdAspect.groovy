package com.ofg.infrastructure.web.correlationid

import com.ofg.infrastructure.correlationid.CorrelationIdHolder
import com.ofg.infrastructure.correlationid.CorrelationIdUpdater
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders

import java.util.concurrent.Callable

import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER

/**
 * Aspect that adds correlation id to
 * 
 * <ul>
 *     <li>{@link org.springframework.web.bind.annotation.RestController} annotated classes 
 *     with public {@link Callable} methods</li>
 *     <li>{@link org.springframework.stereotype.Controller} annotated classes 
 *     with public {@link Callable} methods</li>
 *     <li>explicit {@link org.springframework.web.client.RestOperations}.exchange(..) method calls</li> 
 * </ul>
 * 
 * For controllers an around aspect is created that wraps the {@link Callable#call()} method execution
 * in {@link com.ofg.infrastructure.correlationid.CorrelationIdUpdater#wrapCallableWithId(Callable)}
 *
 * For {@link org.springframework.web.client.RestOperations} we are wrapping all executions of the
 * <b>exchange</b> methods and we are extracting {@link HttpHeaders} from the passed {@link HttpEntity}.
 * Next we are adding correlation id header {@link com.ofg.infrastructure.correlationid.CorrelationIdHolder#CORRELATION_ID_HEADER} with
 * the value taken from {@link com.ofg.infrastructure.correlationid.CorrelationIdHolder}. Finally the method execution proceeds.
 * 
 * @see org.springframework.web.bind.annotation.RestController
 * @see org.springframework.stereotype.Controller
 * @see org.springframework.web.client.RestOperations
 * @see com.ofg.infrastructure.correlationid.CorrelationIdHolder
 * @see CorrelationIdFilter
 * 
 */
@Aspect
@Slf4j
@CompileStatic
class CorrelationIdAspect {

    private static final int HTTP_ENTITY_PARAM_INDEX = 2

    @Pointcut("@target(org.springframework.web.bind.annotation.RestController)")
    private void anyRestControllerAnnotated() {}

    @Pointcut("@target(org.springframework.stereotype.Controller)")
    private void anyControllerAnnotated() {}

    @Pointcut("execution(public java.util.concurrent.Callable *(..))")
    private void anyPublicMethodReturningCallable() {}

    @Pointcut("(anyRestControllerAnnotated() || anyControllerAnnotated()) && anyPublicMethodReturningCallable()")
    private void anyControllerOrRestControllerWithPublicAsyncMethod() {}
    
    @Around('anyControllerOrRestControllerWithPublicAsyncMethod()')
    Object wrapWithCorrelationId(ProceedingJoinPoint pjp) throws Throwable {
        Callable callable = pjp.proceed() as Callable
        log.debug("Wrapping callable with correlation id [${CorrelationIdHolder.get()}]")
        return CorrelationIdUpdater.wrapCallableWithId {
            callable.call()
        }
    }

    @Pointcut('execution(public * org.springframework.web.client.RestOperations.exchange(..))')
    private void anyExchangeRestOperationsMethod() {}
    
    @Around('anyExchangeRestOperationsMethod()')
    Object wrapWithCorrelationIdForRestOperations(ProceedingJoinPoint pjp) throws Throwable {
        String correlationId = CorrelationIdHolder.get()
        log.debug("Wrapping RestTemplate call with correlation id [$correlationId]")
        HttpEntity httpEntity = pjp.args[HTTP_ENTITY_PARAM_INDEX] as HttpEntity
        HttpEntity newHttpEntity = createNewHttpEntity(httpEntity, correlationId)
        List<Object> newArgs = modifyHttpEntityInMethodArguments(pjp, newHttpEntity)
        return pjp.proceed(newArgs.toArray())
    }

    private HttpEntity createNewHttpEntity(HttpEntity httpEntity, String correlationId) {
        HttpHeaders newHttpHeaders = new HttpHeaders()
        newHttpHeaders.putAll(httpEntity.headers)
        newHttpHeaders.add(CORRELATION_ID_HEADER, correlationId)
        return new HttpEntity(httpEntity.body, newHttpHeaders)
    }

    private List<Object> modifyHttpEntityInMethodArguments(ProceedingJoinPoint pjp, HttpEntity newHttpEntity) {
        List<Object> newArgs = []
        pjp.args.eachWithIndex { Object arg, int i ->
            if (i != HTTP_ENTITY_PARAM_INDEX) {
                newArgs.add(i, arg)
            } else {
                newArgs.add(i, newHttpEntity)
            }
        }
        return newArgs
    }
}
