package com.ofg.infrastructure.web.resttemplate.fluent

import com.ofg.infrastructure.discovery.ServiceAlias
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.infrastructure.discovery.ServicePath
import com.ofg.infrastructure.discovery.ServiceResolver
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders
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
 * <pre>
 * serviceRestClient.forService('users').post()
 *                                      .onUrl('/some/url/to/service')
 *                                      .body('<loan><id>100</id><name>Smith</name></loan>')
 *                                      .withHeaders()
 *                                          .contentTypeXml()
 *                                      .andExecuteFor()
 *                                      .aResponseEntity()
 *                                      .ofType(String)
 * </pre>
 *
 * If you want to send a request to the outside world you can also profit from this component as follows (example for google.com):
 *
 *<pre>
 * serviceRestClient.forExternalService().get()
 *                                      .onUrl('http://google.com')
 *                                      .andExecuteFor()
 *                                      .aResponseEntity()
 *                                      .ofType(String)
 * </pre>
 *
 * This client has built in retrying mechanism supported:
 *
 * <pre>
 * @@Autowired
 * AsyncRetryExecutor executor
 *
 * serviceRestClient
 *         .forExternalService()
 *         .retryUsing(
 *             executor
 *                     .withMaxRetries(5)
 *                     .withFixedBackoff(2_000)
 *                     .withUniformJitter())
 *         .delete()
 *         .onUrl(SOME_SERVICE_URL)
 *         .ignoringResponseAsync()
 * </pre>
 *
 * If you are using retry mechanism, another features is enabled - asynchronous invocation. By appending <code>Async</code>
 * to last method you will get <code>ListenableFuture</code> instance. This way you can easily run multiple requests
 * concurrently, combine them, etc.
 *
 * @see <a href="https://github.com/4finance/micro-deps">micro-deps project</a>
 * @see <a href="https://github.com/nurkiewicz/async-retry">async-retry</a>
 */
@CompileStatic
class ServiceRestClient {

    private final RestOperations restOperations
    private final ServiceResolver serviceResolver
    private final ServiceConfigurationResolver configurationResolver

    ServiceRestClient(RestOperations restOperations, ServiceResolver serviceResolver, ServiceConfigurationResolver configurationResolver) {
        this.configurationResolver = configurationResolver
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
        final Map serviceSettings = configurationResolver.dependencies[serviceName] as Map
        final PredefinedHttpHeaders predefinedHeaders = new PredefinedHttpHeaders(serviceSettings)
        final ServicePath path = serviceResolver.resolveAlias(new ServiceAlias(serviceName))
        final URI uri = serviceResolver.fetchUri(path)
        return new HttpMethodBuilder(uri.toString(), restOperations, predefinedHeaders)
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
