package com.ofg.infrastructure.correlationid

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import org.slf4j.MDC

import java.util.concurrent.Callable

/**
 * Groovy utility methods for updating all necessary components with new value
 * of correlation id.
 *
 * @see CorrelationIdUpdater
 */
@Slf4j
@CompileStatic
class CorrelationIdUpdaterUtil {

    /**
     * Temporarily updates correlation ID inside block of code.
     * Makes sure previous ID is restored after block's execution
     *
     * @param temporaryCorrelationId
     * @param block Closure to be executed with new ID
     * @return
     */
    static <T> T withId(String temporaryCorrelationId, Closure<T> block) {
        final String oldCorrelationId = CorrelationIdHolder.get()
        try {
            CorrelationIdUpdater.updateCorrelationId(temporaryCorrelationId)
            return block()
        } finally {
            CorrelationIdUpdater.updateCorrelationId(oldCorrelationId)
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
        final String temporaryCorrelationId = CorrelationIdHolder.get()
        return { Object[] args ->
            final String oldCorrelationId = CorrelationIdHolder.get()
            try {
                CorrelationIdUpdater.updateCorrelationId(temporaryCorrelationId)
                return block(*args)
            } finally {
                CorrelationIdUpdater.updateCorrelationId(oldCorrelationId)
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
     * Object wrapWithCorrelationId(ProceedingJoinPoint pjp) throws Throwable {
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
        final String temporaryCorrelationId = CorrelationIdHolder.get()
        return new Callable() {     //Cannot use `new Callable<T>()` as it fails with Groovyc (works fine with Javac)
            @Override
            Object call() throws Exception {
                final String oldCorrelationId = CorrelationIdHolder.get()
                try {
                    CorrelationIdUpdater.updateCorrelationId(temporaryCorrelationId)
                    return block.call()
                } finally {
                    CorrelationIdUpdater.updateCorrelationId(oldCorrelationId)
                }
            }
        }
    }
}
