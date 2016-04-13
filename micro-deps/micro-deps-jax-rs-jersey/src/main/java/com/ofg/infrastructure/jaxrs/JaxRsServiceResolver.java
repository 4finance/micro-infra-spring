package com.ofg.infrastructure.jaxrs;

import com.google.common.base.Supplier;
import com.ofg.infrastructure.discovery.ServiceAlias;

/**
 * Allows to obtain JAX-RS proxy service interface implementation from its interface
 */
public interface JaxRsServiceResolver {
    /**
     * Obtains proxy resolver for specified remote service class.
     * The service path for the class will be derived from the service class package so this method can be used only
     * for those classes that follow this contract. For classes that don't, specify alias as a second argument explicitly.
     *
     * @param clazz JAX-RS interface to be instantiated
     * @return Function that will resolve service alias into proxy calling remote methods. The function is heavyweight,
     * thread-safe object that can be reused, but the proxies it produces are not thread-safe.
     */
    <T> Supplier<T> getLocator(Class<T> clazz);

    /**
     * Obtains proxy resolver for specified remote service class.
     *
     * @param clazz JAX-RS interface to be instantiated
     * @param alias service alias for resource
     * @return Function that will resolve service alias into proxy calling remote methods. The function is heavyweight,
     * thread-safe object that can be reused, but the proxies it produces are not thread-safe.
     */
    <T> Supplier<T> getLocator(Class<T> clazz, ServiceAlias alias);
}
