package com.ofg.infrastructure.correlationid

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import org.slf4j.MDC
import org.springframework.cloud.sleuth.Span
import org.springframework.cloud.sleuth.TraceContextHolder

import java.util.concurrent.Callable

import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER
/**
 * Class that takes care of updating all necessary components with new value
 * of correlation id.
 * It sets correlationId on {@link ThreadLocal} in {@link CorrelationIdHolder}
 * and in {@link MDC}.
 *
 * @see CorrelationIdHolder
 * @see MDC
 */
@Slf4j
@CompileStatic
class CorrelationIdUpdater {

    static void updateCorrelationId(Span span) {
        log.debug("Updating correlationId with value: [$span.traceId]")
        TraceContextHolder.setCurrentSpan(span)
        MDC.put(CORRELATION_ID_HEADER, span.traceId)
    }

    /**
     * Temporarily updates correlation ID inside block of code.
     * Makes sure previous ID is restored after block's execution
     *
     * @param span
     * @param block Closure to be executed with new ID
     * @return
     */
    static <T> T withId(Span span, Callable<T> block) {
        final Span oldSpan = TraceContextHolder.currentSpan
        try {
            updateCorrelationId(span)
            return block.call()
        } finally {
            updateCorrelationId(oldSpan)
        }
    }

    /**
     * Wraps given {@link Closure} with another {@link Closure} propagating correlation ID inside nested
     * block and passing input parameters.
     *
     * <p/>
     * Useful in a situation when a block is implicit called in a separate thread, for example with GPars.
     *
     * <pre><code>
     * List<Location> extractedLocations = [] as ConcurrentArrayList
     *
     * GParsPool.withPool {
     *     tweets.eachParallel CorrelationIdUpdater.wrapClosureWithId { Tweet tweet ->
     *         extractedLocations << locationExtractor.fromTweet(tweet)
     *     }
     * }
     * </code></pre>
     *
     * @param block code block to execute in a thread with a correlation ID taken from the original thread
     * @return wrapping block as Closure
     * @since 0.8.4
     */
    @CompileStatic(TypeCheckingMode.SKIP)
    static <T> Closure<T> wrapClosureWithId(Closure<T> block) {
        final Span span = TraceContextHolder.currentSpan
        return { Object[] args ->
            final Span oldSpan = TraceContextHolder.currentSpan
            try {
                updateCorrelationId(span)
                return block(*args)
            } finally {
                updateCorrelationId(oldSpan)
            }
        }
    }

    /**
     * Wraps given {@link Callable} (or {@link Closure}) with another {@linke Callable Callable} propagating correlation ID inside nested
     * Callable/Closure.
     *
     * <p/>
     * Useful in a situation when a Callable should be executed in a separate thread, for example in aspects.
     *
     * <pre><code>
     * &#64;Around('...')
     * Object wrapCallableWithCorrelationId(ProceedingJoinPoint pjp) throws Throwable {
     *     Callable callable = pjp.proceed() as Callable
     *     return CorrelationIdUpdater.wrapCallableWithId {
     *         callable.call()
     *     }
     * }
     * </code></pre>
     *
     * <b>Note</b>: Passing only one input parameter currently is supported.
     *
     * @param block code block to execute in a thread with a correlation ID taken from the original thread
     * @return wrapping block as Callable
     * @since 0.8.4
     */
    static <T> Callable<T> wrapCallableWithId(Callable<T> block) {
        final Span span = TraceContextHolder.getCurrentSpan()
        return new Callable() {     //Cannot use `new Callable<T>()` as it fails with Groovyc (works fine with Javac)
            @Override
            Object call() throws Exception {
                final Span oldSpan = TraceContextHolder.getCurrentSpan()
                try {
                    updateCorrelationId(span)
                    return block.call()
                } finally {
                    updateCorrelationId(oldSpan)
                }
            }
        }
    }
}
