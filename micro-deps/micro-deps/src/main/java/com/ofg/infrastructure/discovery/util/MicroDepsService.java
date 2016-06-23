package com.ofg.infrastructure.discovery.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver;
import com.ofg.infrastructure.discovery.ServiceResolver;
import com.ofg.infrastructure.discovery.ZookeeperServiceResolver;
import com.ofg.infrastructure.discovery.watcher.DependencyWatcher;
import com.ofg.infrastructure.discovery.watcher.DependencyWatcherListener;
import com.ofg.infrastructure.discovery.watcher.presence.DefaultDependencyPresenceOnStartupVerifier;
import org.apache.commons.io.IOUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Class that registers microservice in Zookeeper server and enables its discovery using Curator framework.
 */
public class MicroDepsService {

    private static final RetryPolicy DEFAULT_RETRY_POLICY = new ExponentialBackoffRetry(50, 20, 500);
    private final ServiceConfigurationResolver configurationResolver;
    private final DependencyWatcher dependencyWatcher;
    private final CuratorFramework curatorFramework;
    private ServiceInstance serviceInstance;
    private final ServiceDiscovery serviceDiscovery;
    @VisibleForTesting final ServiceResolver serviceResolver;

    /**
     * Creates new instance of the class and registers microservice based on provided {@code microserviceConfig} in Zookeepaer server located at {@code zookeperUrl}.
     *
     * @param zookeeperUrl        URL to running Zookeeper instance
     * @param microserviceContext registration context of microservice
     * @param microserviceUrl     address of the microservice
     * @param microservicePort    port of the microservice
     * @param microserviceConfig  configuration of microservice, by default provided from {@code microservice.json} file placed on classpath
     * @param uriSpec             specification of connection URI to microservice, by default {@code http://microserviceUrl:microservicePort/microserviceContext} is used
     * @param retryPolicy         retry policy to connect to Zookeeper server, by default {@code ExponentialBackoffRetry} is used
     */
    public MicroDepsService(String zookeeperUrl,
                            String microserviceContext,
                            String microserviceUrl,
                            int microservicePort,
                            String microserviceConfig,
                            String uriSpec,
                            RetryPolicy retryPolicy) {
        curatorFramework = CuratorFrameworkFactory.newClient(zookeeperUrl, retryPolicy);
        configurationResolver = new ServiceConfigurationResolver(microserviceConfig);
        try {
            serviceInstance = ServiceInstance.builder()
                    .uriSpec(new UriSpec(uriSpec))
                    .address(microserviceUrl)
                    .port(microservicePort)
                    .name(configurationResolver.getMicroservicePath().getPath())
                    .build();
        } catch (Exception e) {
            Throwables.propagate(e);
        }
        serviceDiscovery = ServiceDiscoveryBuilder.builder(Map.class).basePath(configurationResolver.getBasePath()).client(curatorFramework).thisInstance(serviceInstance).build();
        dependencyWatcher = new DependencyWatcher(configurationResolver.getDependencies(), serviceDiscovery, new DefaultDependencyPresenceOnStartupVerifier());
        serviceResolver = new ZookeeperServiceResolver(configurationResolver, serviceDiscovery, curatorFramework, new ProviderStrategyFactory());
    }

    /**
     * Creates new instance of the class and registers microservice based on provided {@code microserviceConfig} in Zookeepaer server located at {@code zookeperUrl}.
     *
     * @param zookeeperUrl        URL to running Zookeeper instance
     * @param microserviceContext registration context of microservice
     * @param microserviceUrl     address of the microservice
     * @param microservicePort    port of the microservice
     * @param microserviceConfig  configuration of microservice, by default provided from {@code microservice.json} file placed on classpath
     * @param uriSpec             specification of connection URI to microservice, by default {@code http://microserviceUrl:microservicePort/microserviceContext} is used
     */
    public MicroDepsService(String zookeeperUrl,
                            String microserviceContext,
                            String microserviceUrl,
                            int microservicePort,
                            String microserviceConfig,
                            String uriSpec) {
        this(zookeeperUrl, microserviceContext, microserviceUrl, microservicePort, microserviceConfig, uriSpec, DEFAULT_RETRY_POLICY);
    }

    /**
     * Creates new instance of the class and registers microservice based on provided {@code microserviceConfig} in Zookeepaer server located at {@code zookeperUrl}.
     *
     * @param zookeeperUrl        URL to running Zookeeper instance
     * @param microserviceContext registration context of microservice
     * @param microserviceUrl     address of the microservice
     * @param microservicePort    port of the microservice
     * @param microserviceConfig  configuration of microservice, by default provided from {@code microservice.json} file placed on classpath
     */
    public MicroDepsService(String zookeeperUrl, String microserviceContext, String microserviceUrl, int microservicePort, String microserviceConfig) {
        this(zookeeperUrl, microserviceContext, microserviceUrl, microservicePort, microserviceConfig, "{scheme}://{address}:{port}/" + microserviceContext, DEFAULT_RETRY_POLICY);
    }

    /**
     * Creates new instance of the class and registers microservice based on provided {@code microserviceConfig} in Zookeepaer server located at {@code zookeperUrl}.
     *
     * @param zookeeperUrl        URL to running Zookeeper instance
     * @param microserviceContext registration context of microservice
     * @param microserviceUrl     address of the microservice
     * @param microservicePort    port of the microservice
     */
    public MicroDepsService(String zookeeperUrl, String microserviceContext, String microserviceUrl, int microservicePort) {
        this(zookeeperUrl, microserviceContext, microserviceUrl, microservicePort, getTextFromStream(), "{scheme}://{address}:{port}/" + microserviceContext, DEFAULT_RETRY_POLICY);
    }

    private static String getTextFromStream() {
        InputStream inputStream = null;
        try {
            inputStream = MicroDepsService.class.getResourceAsStream("/microservice.json");
            return IOUtils.toString(inputStream);
        } catch (IOException e) {
            Throwables.propagate(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return null;
    }

    public void registerDependencyStateChangeListener(DependencyWatcherListener listener) {
        dependencyWatcher.registerDependencyStateChangeListener(listener);
    }

    public MicroDepsService start() {
        curatorFramework.start();
        try {
            serviceDiscovery.start();
            dependencyWatcher.registerDependencies();
        } catch (Exception e) {
            Throwables.propagate(e);
        }
        serviceResolver.start();
        return this;
    }

    public void stop() {
        serviceResolver.close();
        try {
            dependencyWatcher.unregisterDependencies();
            serviceDiscovery.close();
        } catch (IOException e) {
            Throwables.propagate(e);
        }
        curatorFramework.close();
    }

    public ServiceResolver getServiceResolver() {
        return serviceResolver;
    }
}
