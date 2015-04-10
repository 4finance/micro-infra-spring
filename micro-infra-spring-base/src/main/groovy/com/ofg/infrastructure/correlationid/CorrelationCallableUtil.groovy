package com.ofg.infrastructure.correlationid

import java.util.concurrent.Callable

class CorrelationCallableUtil {

    static <T> Callable<T> withCorrelationId(Closure closure) {
        return new CorrelationCallable(closure)
    }
}
