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

    @Pointcut("@target(org.springframework.web.bind.annotation.RestController))")
    private void anyRestControllerAnnotated() {}

    @Pointcut("@target(org.springframework.stereotype.Controller))")
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

    @Pointcut('execution(public * org.springframework.web.client.RestTemplate.exchange(..))')
    private void anyExchangeRestTemplateMethod() {}
    
    @Around('anyExchangeRestTemplateMethod()')
    void wrapWithCorrelationIdForRestTemplate(ProceedingJoinPoint pjp) throws Throwable {
        log.debug("Wrapping RestTemplate call with correlation id [${CorrelationIdHolder.get()}]")
        HttpEntity httpEntity = pjp.args[HTTP_ENTITY_PARAM_INDEX] as HttpEntity
        HttpEntity newHttpEntity = createNewHttpEntity(httpEntity)
        List<Object> newArgs = modifyHttpEntityInMethodArguments(pjp, newHttpEntity)
        pjp.proceed(newArgs.toArray())
    }

    private HttpEntity createNewHttpEntity(HttpEntity httpEntity) {
        HttpHeaders newHttpHeaders = new HttpHeaders()
        newHttpHeaders.putAll(httpEntity.headers)
        newHttpHeaders.add(CORRELATION_ID_HEADER, CorrelationIdHolder.get())
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
