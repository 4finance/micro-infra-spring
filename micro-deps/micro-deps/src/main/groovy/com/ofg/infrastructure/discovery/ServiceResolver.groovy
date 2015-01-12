package com.ofg.infrastructure.discovery

import com.google.common.base.Optional

/**
 * Provides the possibility of resolving dependency name to an address of
 * a microservice.
 * 
 * {@see ServiceConfigurationResolver}
 */
interface ServiceResolver extends AutoCloseable {

    /**
     * Translates from {@link ServiceAlias} to {@link ServicePath}.
     * For convenience fully qualified hierarchical service names are aliased in microservice.json. This method allows
     * translating from alias to full, globally unique name.
     *
     * @param alias Symbolic name of one of our collaborators, as found in microservice.json
     * @return Path to service, fully qualified name of microservice
     */
    ServicePath resolveAlias(ServiceAlias alias)

    /**
     * Returns address of an arbitrary microservice in the system.
     *
     * @param service - path to microservice, as defined in service registry (<em>this</em> attribute in microservice.json)
     * @return {@see com.google.common.base.Optional} that may contain the address of the microservice
     */
    Optional<URI> getUri(ServicePath service)

    /**
     * Same as {@link ServiceResolver#getUri(com.ofg.infrastructure.discovery.ServicePath)} but fails if no instance is available.
     *
     * @param service - path to microservice
     * @return address of the microservice
     * @throws ServiceUnavailableException - if microservice is unavailable
     */
    URI fetchUri(ServicePath service) throws ServiceUnavailableException

    /**
     * Returns all existing instances of arbitrary running service.
     * @return Empty set if no instance is currently running
     */
    Set<URI> fetchAllUris(ServicePath service);

    /**
     * Returns names of microservices this service depends on.
     * Use e.g. {@link ServiceResolver#fetchAllUris(com.ofg.infrastructure.discovery.ServicePath)} to fetch physical addresses
     * of each and every microservice (path).
     *
     * @return map from name (alias) to path in registry
     */
    Set<ServicePath> fetchMyDependencies()

    /**
     * Returns symbolic names (paths) of all services existing in service registry.
     * @see {@link ServiceResolver#fetchMyDependencies()} to get only our dependencies, not all in system.
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
