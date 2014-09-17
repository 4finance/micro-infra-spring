package com.ofg.infrastructure.reactor

import com.ofg.infrastructure.web.filter.correlationid.CorrelationIdUpdater
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import reactor.event.Event

import static com.ofg.infrastructure.web.filter.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER

/**
 * Aspect around public methods annotated with {@link reactor.spring.annotation.Selector}
 * It will ensure that before the logic of the event processing gets executed, the
 * correlationId will be retrieved from {@link Event.Headers} and all the necessary
 * components will get updated with new correlationId.
 *
 * @see CorrelationIdUpdater
 */
@CompileStatic
@Aspect
@Slf4j
class ReactorAspect {

    @Pointcut(value = "execution(* *(..))")
    private void anyMethod() {}

    @Pointcut(value = "@annotation(reactor.spring.annotation.Selector)")
    private void selectorAnnotated() {}

    @Pointcut("anyMethod() && selectorAnnotated()")
    private void anySelectorAnnotatedMethod() {}

    @Around('anySelectorAnnotatedMethod()')
    Object wrapWithCorrelationId(ProceedingJoinPoint pjp) throws Throwable {
        Object eventArgument = pjp.args.find { it instanceof Event }
        if (!eventArgument) {
            return pjp.proceed()
        }
        Event event = eventArgument as Event
        String correlationId = event.headers.get(CORRELATION_ID_HEADER) as String
        CorrelationIdUpdater.updateCorrelationId(correlationId)
        log.debug("Set correlationId retrieved from event header to [$correlationId]")
        return pjp.proceed()
    }

}
