package com.ofg.infrastructure.web.resttemplate.fluent;

import com.ofg.infrastructure.discovery.*;
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders;
import groovy.transform.CompileStatic;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.util.concurrent.Callable;

/**
 * Abstraction over {@link RestOperations} that provides a fluent API for accessing HTTP resources.
 * It's bound with {@link ServiceResolver} that allows to easily access the microservice collaborators.
 * <p/>
 * You can call a collaborator 'users' defined in microservice descriptor for example named 'microservice.json' as follows
 * <p/>
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
 * <p/>
 * in the following manner (example for POST):
 * <p/>
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
 * <p/>
 * If you want to send a request to the outside world you can also profit from this component as follows (example for google.com):
 * <p/>
 * <pre>
 * serviceRestClient.forExternalService().get()
 *                                      .onUrl('http://google.com')
 *                                      .andExecuteFor()
 *                                      .aResponseEntity()
 *                                      .ofType(String)
 * </pre>
 * <p/>
 * This client has built in retrying mechanism supported:
 * <p/>
 * <pre>
 *
 * @@Autowired AsyncRetryExecutor executor
 * <p/>
 * serviceRestClient
 * .forExternalService()
 * .retryUsing(
 * executor
 * .withMaxRetries(5)
 * .withFixedBackoff(2_000)
 * .withUniformJitter())
 * .delete()
 * .onUrl(SOME_SERVICE_URL)
 * .ignoringResponseAsync()
 * </pre>
 * <p/>
 * If you are using retry mechanism, another features is enabled - asynchronous invocation. By appending {@code Async}
 * to last method you will get {@code ListenableFuture} instance. This way you can easily run multiple requests
 * concurrently, combine them, etc.
 * @see <a href="https://github.com/nurkiewicz/async-retry">async-retry</a>
 */
@CompileStatic
public class ServiceRestClient {
    public ServiceRestClient(RestOperations restOperations, ServiceResolver serviceResolver, ServiceConfigurationResolver configurationResolver) {
        this.configurationResolver = configurationResolver;
        this.restOperations = restOperations;
        this.serviceResolver = serviceResolver;
    }

    /**
     * Returns fluent api to send requests to given collaborating service
     *
     * @deprecated since 0.9.1, use {@link #forService(ServiceAlias serviceAlias)} instead
     *
     * @param serviceName - name of collaborating service from microservice configuration file
     * @return builder for the specified HttpMethod
     */
    @Deprecated
    public HttpMethodBuilder forService(String serviceName) {
        return forService(new ServiceAlias(serviceName));
    }

    /**
     * Returns fluent api to send requests to given collaborating service
     *
     * @param serviceAlias - collaborating service alias as defined in microservice configuration file
     * @return builder for the specified HttpMethod
     */
    public HttpMethodBuilder forService(ServiceAlias serviceAlias) {
        final MicroserviceConfiguration.Dependency dependency = configurationResolver.getDependency(serviceAlias);
        final PredefinedHttpHeaders predefinedHeaders = new PredefinedHttpHeaders(dependency);
        return new HttpMethodBuilder(getServiceUri(serviceAlias), restOperations, predefinedHeaders);
    }

    /**
     * Lazy evaluation of the service's URI
     */
    private Callable<String> getServiceUri(final ServiceAlias serviceAlias) {
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                final URI uri = serviceResolver.fetchUri(serviceAlias);
                return uri.toString();
            }
        };
    }

    /**
     * Returns fluent api to send requests to external service
     *
     * @return builder for the specified HttpMethod
     */
    public HttpMethodBuilder forExternalService() {
        return new HttpMethodBuilder(restOperations);
    }

    private final RestOperations restOperations;
    private final ServiceResolver serviceResolver;
    private final ServiceConfigurationResolver configurationResolver;
}
