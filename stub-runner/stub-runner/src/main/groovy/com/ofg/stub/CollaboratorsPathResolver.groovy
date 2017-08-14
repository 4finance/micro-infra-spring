package com.ofg.stub
import com.google.common.util.concurrent.ListenableFuture
import com.nurkiewicz.asyncretry.AsyncRetryExecutor
import com.nurkiewicz.asyncretry.RetryExecutor
import com.ofg.infrastructure.discovery.IgnorePayloadInstanceSerializer
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.stub.server.ZookeeperServer
import groovy.json.JsonOutput
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.state.ConnectionState
import org.apache.curator.x.discovery.*
import org.apache.curator.x.discovery.details.ServiceCacheListener
import org.apache.http.conn.HttpHostConnectException

import java.util.concurrent.*
/**
 * Class that resolves your service's collaborators from Zookeeper
 */
@Slf4j
@PackageScope
@CompileStatic
class CollaboratorsPathResolver {

    private static final String OLD_COLLABORATORS_ENDPOINT = '/microservice.json'
    private static final String COLLABORATORS_ENDPOINT = '/microserviceDescriptor'
    private static final String APPLICATION_JSON_CONTENT_TYPE = 'application/json'

    /**
     * Resolves the service's collaborators from Zookeeper
     *
     * @param serviceName - the name of the service for which you would like to grab your dependencies (e.g. 'com/ofg/foo/bar')
     * @param context - the realm in which the service is registered (e.g. 'pl')
     * @param zookeeperServer - the ZookeeperServer where your service is registered
     * @return - collaborators of your service
     */
    static ServiceConfigurationResolver resolveFromZookeeper(String serviceName, String context, ZookeeperServer zookeeperServer, StubRunnerOptions config) {
        log.info("Resolving collaborators for service name: '{}' with context: '{}'", serviceName, context)
        return resolveServiceDependenciesFromZookeeper(context, zookeeperServer, serviceName, config)
    }

    private static ServiceConfigurationResolver resolveServiceDependenciesFromZookeeper(String context, ZookeeperServer zookeeperServer, String serviceName, StubRunnerOptions config) {
        return ServiceDiscoveryBuilder.builder(Map)
                .basePath(context)
                .client(zookeeperServer.curatorFramework)
                .serializer(new IgnorePayloadInstanceSerializer(Map.class))
                .build()
                .withCloseable { ServiceDiscovery discovery ->
            discovery.start()
            String uriSpec = obtainServiceInstanceUri(discovery, serviceName, config)
            log.info("Resolved service instance Uri: '{}' for service name: '{}' with context: '{}'", uriSpec, serviceName, context)
            ListenableFuture<String> microserviceDescriptor = getMicroserviceDescriptor(uriSpec, config.waitTimeout)
            return new ServiceConfigurationResolver(microserviceDescriptor.get())
        }
    }

    private static String obtainServiceInstanceUri(ServiceDiscovery discovery, String serviceName, StubRunnerOptions config) {
        return discovery.serviceProviderBuilder().serviceName(serviceName).build().withCloseable { ServiceProvider serviceProvider ->
            serviceProvider.start()
            ServiceInstance<Map> instance = serviceProvider.instance
            if (!instance && config.waitForServiceConnect) {
                instance = waitAndGetService(discovery, serviceName, config.waitTimeout)
            }
            return instance.buildUriSpec()
        }
    }

    private static ServiceInstance<Map> waitAndGetService(ServiceDiscovery discovery, String serviceName, Integer waitTimeout) {
        return discovery.serviceCacheBuilder().name(serviceName).build().withCloseable { ServiceCache serviceCache ->
            serviceCache.start()
            CompletableFuture<ServiceInstance<Map>> futureServiceInstance = new CompletableFuture<>()
            serviceCache.addListener(new TimeoutServiceCacheListener(serviceCache, futureServiceInstance))
            log.info("Registering listener and waiting {} seconds for service instance with name '{}' to connect", waitTimeout, serviceName)
            ServiceInstance<Map> instance = futureServiceInstance.get(waitTimeout, TimeUnit.SECONDS)
            log.info("Service instance resolved for service name '{}'", serviceName)
            return instance
        }
    }

    private static final class TimeoutServiceCacheListener implements ServiceCacheListener {

        private final ServiceCache serviceCache
        private final CompletableFuture<ServiceInstance<Map>> futureServiceInstance

        TimeoutServiceCacheListener(ServiceCache serviceCache, CompletableFuture<ServiceInstance<Map>> futureServiceInstance) {
            this.serviceCache = serviceCache
            this.futureServiceInstance = futureServiceInstance
        }

        @Override
        void cacheChanged() {
            log.debug("Service cache changed")
            List<ServiceInstance> instances = serviceCache.getInstances()
            log.debug("Service cache returned instances: {}", instances)
            instances.stream().findFirst().ifPresent({ ServiceInstance<Map> instance ->
                log.debug("Setting service instance: {}", instance)
                futureServiceInstance.complete(instance)
            })
        }

        @Override
        void stateChanged(CuratorFramework client, ConnectionState newState) {
            log.debug("Service cache state changed")
            // It's not called anytime
        }
    }

    @CompileDynamic
    @TypeChecked
    static ListenableFuture<String> getMicroserviceDescriptor(String uriSpec, Integer timeout) {
        final HTTPBuilder httpRequest = new HTTPBuilder(uriSpec)
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        RetryExecutor executor = new AsyncRetryExecutor(scheduler)
        if (timeout) {
            executor.retryOn(HttpHostConnectException, ConnectException).
                    withExponentialBackoff(500, 2).     //500ms times 2 after each retry
                    withMaxDelay(timeout * 1000)      //timeout in seconds
        }
        return executor.getWithRetry(new Callable<String>() {
            @Override
            String call() throws Exception {
                // TODO: add support for new collaborators endpoint
                return JsonOutput.toJson(httpRequest.get(path: OLD_COLLABORATORS_ENDPOINT, contentType: APPLICATION_JSON_CONTENT_TYPE))
            }
        })
    }
}
