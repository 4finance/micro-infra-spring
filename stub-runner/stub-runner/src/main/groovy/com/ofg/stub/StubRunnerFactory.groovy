package com.ofg.stub

import com.google.common.base.Optional
import com.ofg.infrastructure.discovery.MicroserviceConfiguration
import com.ofg.infrastructure.discovery.MicroserviceConfiguration.Dependency.StubsConfiguration
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.stub.mapping.ProjectMetadata
import com.ofg.stub.registry.StubRegistry
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import org.apache.curator.RetryPolicy
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry

import static com.google.common.base.Preconditions.checkArgument
import static org.apache.commons.lang.StringUtils.*

@Slf4j
@CompileStatic
@PackageScope
class StubRunnerFactory {

    private static final RetryPolicy RETRY_POLICY = new ExponentialBackoffRetry(50, 20, 500)

    private final StubRunnerOptions stubRunnerOptions
    private final ServiceConfigurationResolver serviceConfigurationResolver
    private final StubDownloader stubDownloader
    private final CuratorFramework client

    StubRunnerFactory(StubRunnerOptions stubRunnerOptions, ServiceConfigurationResolver serviceConfigurationResolver) {
        this(stubRunnerOptions, serviceConfigurationResolver, CuratorFrameworkFactory.newClient(stubRunnerOptions.zookeeperConnectString, RETRY_POLICY), new StubDownloader())
    }

    @PackageScope
    StubRunnerFactory(StubRunnerOptions stubRunnerOptions, ServiceConfigurationResolver serviceConfigurationResolver,
                      CuratorFramework client, StubDownloader stubDownloader) {
        this.stubRunnerOptions = stubRunnerOptions
        this.client = client
        this.serviceConfigurationResolver = serviceConfigurationResolver
        this.stubDownloader = stubDownloader
    }

    List<Optional<StubRunner>> createStubsFromServiceConfiguration() {
        checkArgument(isNotEmpty(stubRunnerOptions.stubRepositoryRoot))
        client.start()
        return serviceConfigurationResolver.dependencies.collect { MicroserviceConfiguration.Dependency dependency ->
            String dependencyMappingsPath = dependency.servicePath.path
            StubsConfiguration stubsConfiguration = dependency.stubs
            final File unzipedStubDir = stubDownloader.downloadAndUnpackStubJar(stubRunnerOptions.skipLocalRepo, stubRunnerOptions.stubRepositoryRoot,
                    stubsConfiguration.stubsGroupId, "$stubsConfiguration.stubsArtifactId-${stubsConfiguration.stubsClassifier}")
            final String context = serviceConfigurationResolver.basePath
            return createStubRunner(unzipedStubDir, stubsConfiguration, context, dependencyMappingsPath)
        }
    }

    List<Optional<StubRunner>> createStubsFromStubsModule() {
        checkArgument(isNotEmpty(stubRunnerOptions.stubRepositoryRoot))
        checkArgument(isNotEmpty(stubRunnerOptions.stubsGroup))
        checkArgument(isNotEmpty(stubRunnerOptions.stubsModule))
        client.start()
        final File unzippedStubsDir = stubDownloader.downloadAndUnpackStubJar(stubRunnerOptions.skipLocalRepo, stubRunnerOptions.stubRepositoryRoot, stubRunnerOptions.stubsGroup, stubRunnerOptions.stubsModule)
        final String context = serviceConfigurationResolver.basePath
        return serviceConfigurationResolver.dependencies.collect { MicroserviceConfiguration.Dependency dependency ->
            String dependencyMappingsPath = dependency.servicePath.path
            return createStubRunner(unzippedStubsDir, dependency.stubs, context, dependencyMappingsPath)
        }
    }

    private Optional createStubRunner(File unzipedStubDir, StubsConfiguration stubsConfiguration, String context, String dependencyMappingsPath) {
        if (!unzipedStubDir) {
            return Optional.absent()
        }
        return Optional.of(createStubRunner(stubsConfiguration.stubsArtifactId, unzipedStubDir, context, dependencyMappingsPath, stubRunnerOptions, client))
    }

    private StubRunner createStubRunner(String alias, File unzippedStubsDir, String context, String dependencyMappingsPath, StubRunnerOptions stubRunnerOptions, CuratorFramework client) {
        List<ProjectMetadata> projects = [new ProjectMetadata(alias, dependencyMappingsPath, context)]
        Arguments arguments = new Arguments(stubRunnerOptions, context, unzippedStubsDir.path, alias, projects)
        return new StubRunner(arguments, new StubRegistry(stubRunnerOptions.zookeeperConnectString, client))
    }

}
