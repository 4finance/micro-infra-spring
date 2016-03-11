package com.ofg.infrastructure.jaxrs;

import com.google.common.base.Supplier;

public interface JaxRsServiceResolver {
    /**
     * Obtains proxy resolver for specified remote service class.
     *
     * @param clazz JAX-RS interface to be instantiated
     * @return Function that will resolve service alias into proxy calling remote methods. The function is heavyweight,
     * thread-safe object that can be reused, but the proxies it produces are not thread-safe.
     */
    <T> Supplier<T> getLocator(Class<T> clazz);
}
