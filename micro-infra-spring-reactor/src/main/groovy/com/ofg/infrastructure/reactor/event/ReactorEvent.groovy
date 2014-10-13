package com.ofg.infrastructure.reactor.event
import com.ofg.infrastructure.correlationid.CorrelationIdHolder
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import reactor.event.Event
import reactor.function.Consumer

import static CorrelationIdHolder.CORRELATION_ID_HEADER

/**
 * Extension to {@link Event} that ensures that {@link CorrelationIdHolder#CORRELATION_ID_HEADER} header ({@link Event.Headers}
 * is set on {@link Event}.
 *
 * @see CorrelationIdHolder
 * @see Event
 *
 * @param < T > - type of passed data
 */
@CompileStatic
@Slf4j
class ReactorEvent<T> extends Event<T> {
    ReactorEvent(Class<T> klass) {
        super(klass)
        setCorrelationIdOnHeaders()
    }

    ReactorEvent(Event.Headers headers, T data) {
        super(headers, data)
        setCorrelationIdOnHeaders()
    }

    ReactorEvent(Event.Headers headers, T data, Consumer<Throwable> errorConsumer) {
        super(headers, data, errorConsumer)
        setCorrelationIdOnHeaders()
    }

    ReactorEvent(T data) {
        super(data)
        setCorrelationIdOnHeaders()
    }

    private void setCorrelationIdOnHeaders() {
        if(!headers.contains(CORRELATION_ID_HEADER)) {
            headers.set(CORRELATION_ID_HEADER, CorrelationIdHolder.get())
        }
    }

    /**
     * Wrap the given object with an {@link Event}.
     *
     * @param obj
     *     The object to wrap.
     *
     * @return The new {@link Event}.
     */
    static <T> Event<T> wrap(T obj) {
        return new ReactorEvent<T>(obj)
    }

    /**
     * Wrap the given object with an {@link Event} and set the {@link Event#getReplyTo() replyTo} to the given {@code
     * replyToKey}.
     *
     * @param obj
     *     The object to wrap.
     * @param replyToKey
     *     The key to use as a {@literal replyTo}.
     * @param <T>
     *     The type of the given object.
     *
     * @return The new {@link Event}.
     */
    static <T> Event<T> wrap(T obj, Object replyToKey) {
        return new ReactorEvent<T>(obj).setReplyTo(replyToKey)
    }
}
