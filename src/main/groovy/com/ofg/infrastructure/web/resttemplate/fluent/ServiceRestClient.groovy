package com.ofg.infrastructure.web.resttemplate.fluent

import com.google.common.base.Optional
import com.ofg.infrastructure.discovery.ServiceResolver
import org.springframework.web.client.RestTemplate

class ServiceRestClient {

    private final RestTemplate restTemplate
    private final ServiceResolver serviceResolver

    public ServiceRestClient(RestTemplate restTemplate, ServiceResolver serviceResolver) {
        this.restTemplate = restTemplate
        this.serviceResolver = serviceResolver
    }

    /**
     * Returns fluent api to send requests to given collaborating service 
     * @param serviceName - name of collaborating service from microservice configuration file
     * @return
     */
    public HttpMethodBuilder forService(String serviceName) {
        Optional<String> serviceUrl = serviceResolver.getUrl(serviceName)
        if (serviceUrl.isPresent()) {
            return new HttpMethodBuilder(serviceUrl.get(), restTemplate)
        }
        throw new ServiceUnavailableException(serviceName)
    }

    /**
     * Returns fluent api to send requests to given host name
     * @param hostName
     * @return
     */
    public HttpMethodBuilder forHost(String hostName) {
        return new HttpMethodBuilder(hostName, restTemplate)
    }

}
