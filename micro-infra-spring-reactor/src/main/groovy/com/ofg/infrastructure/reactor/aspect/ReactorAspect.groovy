package com.ofg.infrastructure.reactor.aspect

import com.ofg.infrastructure.correlationid.CorrelationIdUpdater
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import reactor.event.Event

import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER

/**
 * Aspect around public methods annotated with {@link reactor.spring.annotation.Selector}
 * It will ensure that before the logic of the event processing gets executed, the
 * correlationId will be retrieved from {@link Event.Headers} and all the necessary
 * components will get updated with new correlationId.
 *
 * Example of usage for the following scenario:
 * <ul>
 *     <li>We have a <b>Config</b> class for SpringBoot autoconfiguration</li>
 *     <li>We have a <b>MySender</b> class that sends an {@link com.ofg.infrastructure.reactor.event.ReactorEvent} using Reactor</li>
 *     <li>We have a <b>MySubscriber</b> class that subscribes to 'key' channel and executes logic from the annotated method</li>
 * </ul>
 *
 * And the code:
 * <pre>
 *     @Configuration
 *     @EnableReactor
 *     @ComponentScan
 *     @EnableAutoConfiguration
 *     class Config {
 *     }
 *
 *     @Component
 *     class MySender {
 *          @Autowired public Reactor reactor
 *
 *          void sendsEvent() {
 *              reactor.notify('key', ReactorEvent.wrap('data'))
 *          }
 *     }
 *
 *     @Component
 *     class MySubscriber {
 *          @Autowired public Reactor reactor
 *
 *          @Selector('key')
 *          void receive(Event<String> event) {
 *             // do some logic upon receiving messages
 *          }
 *
 *          Reactor getReactor() {
 *              return reactor
 *          }
 *     }
 * </pre>
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
        return CorrelationIdUpdater.withId(correlationId) {
            log.debug("Set correlationId retrieved from event header to [$correlationId]")
            return pjp.proceed()
        }
    }

}
