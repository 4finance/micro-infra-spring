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
     * 
     * @param dependency - alias from microservice configuration {@see ServiceConfigurationResolver}
     * @return {@see com.google.common.base.Optional} that may contain the address of the microservice
     */
    Optional<String> getUrl(String dependency)

    /**
     * Start service resolver (e.g. start all service providers)
     */
    void start()

    /**
     * Close service resolver (e.g. close all service providers)
     */
    void close()
}
