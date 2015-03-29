package com.ofg.stub

import com.google.common.util.concurrent.ListenableFuture
import com.nurkiewicz.asyncretry.AsyncRetryExecutor
import com.nurkiewicz.asyncretry.RetryExecutor
import com.ofg.stub.server.ZookeeperServer
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

    private static final String COLLABORATORS_ENDPOINT = '/collaborators'
    private static final String APPLICATION_JSON_CONTENT_TYPE = 'application/json'

    /**
     * Resolves the service's collaborators from Zookeeper
     *
     * @param serviceName - the name of the service for which you would like to grab your dependencies (e.g. 'com/ofg/foo/bar')
     * @param context - the realm in which the service is registered (e.g. 'pl')
     * @param zookeeperServer - the ZookeeperServer where your service is registered
     * @return - collaborators of your service
     */
    static Collaborators resolveFromZookeeper(String serviceName, String context, ZookeeperServer zookeeperServer, StubRunnerOptions config) {
        List<String> dependencies = resolveServiceDependenciesFromZookeeper(context, zookeeperServer, serviceName, config)
        return new Collaborators(context, dependencies)
    }

    private
    static List<String> resolveServiceDependenciesFromZookeeper(String context, ZookeeperServer zookeeperServer, String serviceName, StubRunnerOptions config) {
        ServiceDiscovery discovery = ServiceDiscoveryBuilder.builder(Void)
                .basePath(context)
                .client(zookeeperServer.curatorFramework)
                .build()
        discovery.start()
        String uriSpec = obtainServiceInstanceUri(discovery, serviceName, config)
        ListenableFuture<Map<String, Map<String, String>>> collaborators = getCollaborators(uriSpec, config.waitTimeout);
        List<String> collaboratorsList = collaborators.get()?.keySet()?.toList()
        discovery?.close()
        return collaboratorsList
    }

    private
    static String obtainServiceInstanceUri(ServiceDiscovery discovery, String serviceName, StubRunnerOptions config) {
        ServiceProvider serviceProvider = discovery.serviceProviderBuilder().serviceName(serviceName).build()
        serviceProvider.start()
        ServiceInstance<Void> instance = serviceProvider.instance
        if (!instance && config.waitForServiceConnect) {
            instance = waitAndGetService(discovery, serviceName, serviceProvider, config.waitTimeout, instance)
        }
        serviceProvider?.close()
        return instance.buildUriSpec()
    }

    private
    static ServiceInstance<Void> waitAndGetService(ServiceDiscovery discovery, String serviceName, ServiceProvider serviceProvider, Integer waitTimeout, ServiceInstance<Void> instance) {
        ServiceCache serviceCache = discovery.serviceCacheBuilder().name(serviceName).build()
        serviceCache.start()
        TimeoutServiceCacheListener listener = new TimeoutServiceCacheListener(serviceProvider)
        serviceCache.addListener(listener)
        instance = listener.get(waitTimeout, TimeUnit.SECONDS)
        return instance
    }

    private static final  class TimeoutServiceCacheListener implements ServiceCacheListener, Future<ServiceInstance<Void>> {

        ServiceProvider serviceProvider

        private static enum State {
            DONE, EMPTY, CANCELLED
        }
        BlockingQueue<ServiceInstance<Void>> blockingQueue = new ArrayBlockingQueue(1)
        volatile State state = State.EMPTY

        TimeoutServiceCacheListener(ServiceProvider serviceProvider) {
            this.serviceProvider = serviceProvider
        }

        @Override
        void cacheChanged() {
            ServiceInstance<Void> instance = serviceProvider.getInstance()
            if (instance) {
                blockingQueue.put(instance)
                state = State.DONE
            }
        }

        @Override
        void stateChanged(CuratorFramework client, ConnectionState newState) {
            // It's not called anytime
        }

        @Override
        boolean cancel(boolean mayInterruptIfRunning) {
            throw new UnsupportedOperationException()
        }

        @Override
        boolean isCancelled() {
            return state == State.CANCELLED
        }

        @Override
        boolean isDone() {
            return state == State.DONE
        }

        @Override
        ServiceInstance<Void> get() throws InterruptedException, ExecutionException {
            return blockingQueue.take()
        }

        @Override
        ServiceInstance<Void> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            ServiceInstance<Void> object = blockingQueue.poll(timeout, unit)
            if (!object) {
                throw new TimeoutException("Service unavailable")
            }
            return object
        }
    }

    @CompileDynamic
    @TypeChecked
    static ListenableFuture<Map<String, Map<String, String>>> getCollaborators(String uriSpec, Integer timeout) {
        final HTTPBuilder httpRequest = new HTTPBuilder(uriSpec)
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        RetryExecutor executor = new AsyncRetryExecutor(scheduler)
        if (timeout) {
            executor.retryOn(HttpHostConnectException, ConnectException).
                    withExponentialBackoff(500, 2).     //500ms times 2 after each retry
                    withMaxDelay(timeout * 1000);       //timeout in seconds;
        }
        return executor.getWithRetry(new Callable<Map<String, Map<String, String>>>() {
            @Override
            Map<String, Map<String, String>> call() throws Exception {
                return httpRequest.get(path: COLLABORATORS_ENDPOINT, contentType: APPLICATION_JSON_CONTENT_TYPE)
            }
        })
    }
}
