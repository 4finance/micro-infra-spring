package com.ofg.infrastructure.correlationid

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.slf4j.MDC
import org.springframework.util.StringUtils

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

    static void updateCorrelationId(String correlationId) {
        if (StringUtils.hasText(correlationId)) {
            log.debug("Updating correlationId with value: [$correlationId]")
            CorrelationIdHolder.set(correlationId)
            MDC.put(CORRELATION_ID_HEADER, correlationId)
        }
    }

    /**
     * Temporarily updates correlation ID inside block of code.
     * Makes sure previous ID is restored after block's execution
     * @param temporaryCorrelationId
     * @param block Closure to be executed with new ID
     * @return
     */
    static <T> T withId(String temporaryCorrelationId, Closure<T> block) {
        final String oldCorrelationId = CorrelationIdHolder.get()
        try {
            updateCorrelationId(temporaryCorrelationId)
            return block()
        } finally {
            updateCorrelationId(oldCorrelationId)
        }
    }

    /**
     * Propagates correlation ID inside nested Closure, passing one input parameter.
     *
     * Useful in situation when a closure is implicit called in a separate thread, for example with GPars.
     *
     * <pre><code>
     * List<Location> extractedLocations = [] as ConcurrentArrayList
     *
     * GParsPool.withPool {
     *     tweets.eachParallel CorrelationIdUpdater.closureWithId { Tweet tweet ->
     *         extractedLocations << locationExtractor.fromTweet(tweet)
     *     }
     * }
     * </code></pre>
     *
     * <b>Note</b>: Passing only one input parameter currently is supported.
     *
     * @param closure code block to execute in a thread with a correlation ID taken from original thread
     * @return wrapping closure
     * @since 0.8.4
     */
    static <T> Closure<T> closureWithId(Closure<T> closure) {
        final String temporaryCorrelationId = CorrelationIdHolder.get()
        return { Object arg ->
            final String oldCorrelationId = CorrelationIdHolder.get()
            try {
                updateCorrelationId(temporaryCorrelationId)
                return closure(arg)
            } finally {
                updateCorrelationId(oldCorrelationId)
            }
        }
    }
}
