package com.ofg.infrastructure.reactor.event;

import com.ofg.infrastructure.correlationid.CorrelationIdHolder;
import reactor.event.Event;
import reactor.function.Consumer;

import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER;

/**
 * Extension to {@link Event} that ensures that {@link CorrelationIdHolder#CORRELATION_ID_HEADER} header ({@link Event.Headers}
 * is set on {@link Event}.
 *
 * @param < T > - type of passed data
 * @see CorrelationIdHolder
 * @see Event
 */
public class ReactorEvent<T> extends Event<T> {
    public ReactorEvent(Class<T> klass) {
        super(klass);
        setCorrelationIdOnHeaders();
    }

    public ReactorEvent(Headers headers, T data) {
        super(headers, data);
        setCorrelationIdOnHeaders();
    }

    public ReactorEvent(Headers headers, T data, Consumer<Throwable> errorConsumer) {
        super(headers, data, errorConsumer);
        setCorrelationIdOnHeaders();
    }

    public ReactorEvent(T data) {
        super(data);
        setCorrelationIdOnHeaders();
    }

    private void setCorrelationIdOnHeaders() {
        if (!getHeaders().contains(CORRELATION_ID_HEADER)) {
            getHeaders().set(CORRELATION_ID_HEADER, CorrelationIdHolder.get());
        }
    }

    /**
     * Wrap the given object with an {@link Event}.
     *
     * @param obj The object to wrap.
     * @return The new {@link Event}.
     */
    public static <T> Event<T> wrap(T obj) {
        return new ReactorEvent<T>(obj);
    }

    /**
     * Wrap the given object with an {@link Event} and set the {@link Event#getReplyTo() replyTo} to the given {@code
     * replyToKey}.
     *
     * @param obj        The object to wrap.
     * @param replyToKey The key to use as a {@literal replyTo}.
     * @param <T>        The type of the given object.
     * @return The new {@link Event}.
     */
    public static <T> Event<T> wrap(T obj, Object replyToKey) {
        return new ReactorEvent<T>(obj).setReplyTo(replyToKey);
    }
}
