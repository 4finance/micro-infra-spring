package com.ofg.infrastructure.discovery;

import com.google.common.base.Optional;

import java.net.URI;
import java.util.Set;

/**
 * Provides the possibility of resolving dependency name to an address of
 * a microservice.
 * <p/>
 * {@see ServiceConfigurationResolver}
 * {@see ServiceAlias}
 * {@see ServicePath}
 */
public interface ServiceResolver extends AutoCloseable {
    /**
     * Translates from {@link ServiceAlias} to {@link ServicePath}.
     * For convenience fully qualified hierarchical service names are aliased in microservice.json. This method allows
     * translating from alias to full, globally unique name.
     * NB: This method will only work for dependencies declared in {@code microservice.json} since aliases
     * are declared only there, locally.
     *
     * @param alias Symbolic name of one of our collaborators, as found in microservice.json
     * @return Path to service, fully qualified name of microservice
     */
    ServicePath resolveAlias(ServiceAlias alias);

    /**
     * Returns address of an arbitrary microservice in the system.
     *
     * @param service - path to microservice, as defined in service registry (<em>this</em> attribute in microservice.json)
     * @return {@see com.google.common.base.Optional} that may contain the address of the microservice
     */
    Optional<URI> getUri(ServicePath service);

    /**
     * Same as {@link ServiceResolver#getUri(ServicePath)} but fails if no instance is available.
     *
     * @param service - path to microservice
     * @return address of the microservice
     * @throws ServiceUnavailableException - if microservice is unavailable
     */
    URI fetchUri(ServicePath service);

    /**
     * Returns all existing instances of arbitrary running service.
     *
     * @return Empty set if no instance is currently running
     */
    Set<URI> fetchAllUris(ServicePath service);

    /**
     * Returns names of microservices this service depends on.
     * Use e.g. {@link ServiceResolver#fetchAllUris(ServicePath)} to fetch physical addresses
     * of each and every microservice (path).
     *
     * @return map from name (alias) to path in registry
     */
    Set<ServicePath> fetchMyDependencies();

    /**
     * Returns symbolic names (paths) of all services existing in service registry.
     *
     * @see {@link ServiceResolver#fetchMyDependencies()} to get only your dependencies, not all in system.
     */
    Set<ServicePath> fetchAllDependencies();

    /**
     * Returns address of microservice
     *
     * @param serviceAlias - service alias as specified in microservice configuration
     * @return {@see com.google.common.base.Optional} that may contain the address of the microservice
     */
    Optional<URI> getUri(ServiceAlias serviceAlias);

    /**
     * Returns address of microservice
     *
     * @param serviceAlias - service alias as specified in microservice configuration
     * @return address of the microservice
     * @throws ServiceUnavailableException - if microservice is unavailable
     */
    URI fetchUri(ServiceAlias serviceAlias);

    /**
     * Start service resolver (e.g. start all service providers)
     */
    void start();

    /**
     * Close service resolver (e.g. close all service providers)
     */
    void close();
}
