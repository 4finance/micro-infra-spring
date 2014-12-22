package com.ofg.infrastructure.discovery

import com.google.common.base.Optional

/**
 * Provides the possibility of resolving dependency name to an address of
 * a microservice.
 * 
 * {@see ServiceConfigurationResolver}
 */
interface ServiceResolver extends AutoCloseable {

    ServicePath resolveAlias(ServiceAlias alias)

    /**
     * Returns address of microservice
     *
     * @param service - alias from microservice configuration {@see ServiceConfigurationResolver}
     * @return {@see com.google.common.base.Optional} that may contain the address of the microservice
     */
    Optional<URI> getUri(ServicePath service)

    /**
     * Returns address of microservice
     *
     * @param service - alias from microservice configuration {@see ServiceConfigurationResolver}
     * @return address of the microservice
     * @throws ServiceUnavailableException - if microservice is unavailable
     */
    URI fetchUri(ServicePath service) throws ServiceUnavailableException

    /**
     * Returns all existing addresses of arbitrary running service.
     */
    Set<URI> fetchAllUris(ServicePath service);

    /**
     * Returns names of microservices this service depends on
     *
     * @return map from name (alias) to path in registry
     */
    Set<ServicePath> fetchMyDependencies()

    /**
     * Returns names of all services existing in service registry
     */
    Set<ServicePath> fetchAllDependencies()

    /**
     * Start service resolver (e.g. start all service providers)
     */
    void start()

    /**
     * Close service resolver (e.g. close all service providers)
     */
    void close()
}
