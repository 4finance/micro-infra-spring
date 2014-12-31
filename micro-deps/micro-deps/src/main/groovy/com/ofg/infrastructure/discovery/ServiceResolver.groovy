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
     * Returns address of microservice
     *
     * @deprecated Use {@link #getUri(com.ofg.infrastructure.discovery.ServicePath)}  instead
     * @param service - alias from microservice configuration {@see ServiceConfigurationResolver}
     * @return {@see com.google.common.base.Optional} that may contain the address of the microservice
     */
    @Deprecated
    Optional<String> getUrl(String service)

    /**
     * Returns address of microservice
     *
     * @deprecated Use {@link #fetchUri(com.ofg.infrastructure.discovery.ServicePath)}
     * @param service - alias from microservice configuration {@see ServiceConfigurationResolver}
     * @return address of the microservice
     * @throws ServiceUnavailableException - if microservice is unavailable
     */
    @Deprecated
    String fetchUrl(String service) throws ServiceUnavailableException

    /**
     * Returns names of microservices this service depends on
     *
     * @deprecated Use {@link #fetchMyDependencies()}
     * @return names of microservices
     */
    @Deprecated
    Set<String> fetchCollaboratorsNames()

    /**
     * Start service resolver (e.g. start all service providers)
     */
    void start()

    /**
     * Close service resolver (e.g. close all service providers)
     */
    void close()
}
