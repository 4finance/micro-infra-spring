package com.ofg.infrastructure.web.filter.correlationid
import groovy.util.logging.Slf4j
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders

import java.util.concurrent.Callable

import static com.ofg.infrastructure.web.filter.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER

@Aspect
@Slf4j
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
        return CorrelationCallable.withCorrelationId {
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
