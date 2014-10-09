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
     * Returns address of microservice
     *
     * @param service - alias from microservice configuration {@see ServiceConfigurationResolver}
     * @return {@see com.google.common.base.Optional} that may contain the address of the microservice
     */
    Optional<String> getUrl(String service)

    /**
     * Returns address of microservice
     *
     * @param service - alias from microservice configuration {@see ServiceConfigurationResolver}
     * @return address of the microservice
     * @throws ServiceNotFoundException - if microservice instance cannot be found
     */
    String fetchUrl(String service)

    /**
     * Start service resolver (e.g. start all service providers)
     */
    void start()

    /**
     * Close service resolver (e.g. close all service providers)
     */
    void close()
}
