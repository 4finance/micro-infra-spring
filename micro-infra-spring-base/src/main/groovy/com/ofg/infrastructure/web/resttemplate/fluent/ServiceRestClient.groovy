package com.ofg.infrastructure.web.resttemplate.fluent

import com.google.common.base.Optional
import com.ofg.infrastructure.discovery.ServiceResolver
import groovy.transform.CompileStatic
import org.springframework.web.client.RestOperations

/**
 * Abstraction over {@link org.springframework.web.client.RestOperations} that provides a fluent API
 * for accessing HTTP resources. It's bound with {@link ServiceResolver} that allows to easily access
 * the microservice collaborators.
 * 
 * You can call a collaborator 'users' defined in microservice descriptor for example named 'microservice.json' as follows
 * 
 * <pre>
 *     {
 *           "prod": {
 *           "this": "foo/bar/registration",
 *           "dependencies": {
 *               "users": "foo/bar/users",
 *               "newsletter": "foo/bar/comms/newsletter",
 *               "confirmation": "foo/bar/security/confirmation"
 *               }
 *           }
 *      }
 * </pre>
 * 
 * in the following manner (example for POST):
 * 
 * serviceRestClient.forService('users').post()
 *                                      .onUrl('/some/url/to/service')
 *                                      .body('<loan><id>100</id><name>Smith</name></loan>')
 *                                      .withHeaders()
 *                                          .contentTypeXml()
 *                                      .andExecuteFor()
 *                                      .aResponseEntity()
 *                                      .ofType(String)
 * 
 * If you want to send a request to the outside world you can also profit from this component as follows (example for google.com):
 *
 * serviceRestClient.forExternalService().get()
 *                                      .onUrl('http://google.com')
 *                                      .andExecuteFor()
 *                                      .aResponseEntity()
 *                                      .ofType(String)
 * 
 * @see <a href="https://github.com/4finance/micro-deps">micro-deps project</a>
 */
@CompileStatic
class ServiceRestClient {

    private final RestOperations restOperations
    private final ServiceResolver serviceResolver

    ServiceRestClient(RestOperations restOperations, ServiceResolver serviceResolver) {
        this.restOperations = restOperations
        this.serviceResolver = serviceResolver
    }

    /**
     * Returns fluent api to send requests to given collaborating service 
     * 
     * @param serviceName - name of collaborating service from microservice configuration file
     * @return builder for the specified HttpMethod
     */
    public HttpMethodBuilder forService(String serviceName) {
        Optional<String> serviceUrl = serviceResolver.getUrl(serviceName)
        if (serviceUrl.isPresent()) {
            return new HttpMethodBuilder(serviceUrl.get(), restOperations)
        }
        throw new ServiceUnavailableException(serviceName)
    }

    /**
     * Returns fluent api to send requests to external service
     * 
     * @return builder for the specified HttpMethod
     */
    public HttpMethodBuilder forExternalService() {
        return new HttpMethodBuilder(restOperations)
    }

}
