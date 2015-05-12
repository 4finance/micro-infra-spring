package com.ofg.infrastructure.correlationid;

/**
 * Component that stores correlation id using {@link ThreadLocal}
 */
//from: https://github.com/daniel-bryant-uk/correlation-id-sync/
public class CorrelationIdHolder {

    public static final String CORRELATION_ID_HEADER = "correlationId";
    private static final ThreadLocal<String> id = new ThreadLocal<String>();

    public static void set(String correlationId) {
        id.set(correlationId);
    }

    public static String get() {
        return id.get();
    }

    public static void remove() {
        id.remove();
    }
}
