package com.ofg.stub

import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.stub.mapping.ProjectMetadata
import com.ofg.stub.registry.StubRegistry
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import org.apache.curator.RetryPolicy
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.curator.test.TestingServer

import static com.google.common.base.Preconditions.checkArgument
import static com.ofg.infrastructure.discovery.ServiceConfigurationProperties.PATH
import static org.apache.commons.lang.StringUtils.*

@Slf4j
class StubRunnerFactory {

    private static final RetryPolicy RETRY_POLICY = new ExponentialBackoffRetry(50, 20, 500)

    private final TestingServer testingServer
    private final ServiceConfigurationResolver serviceConfigurationResolver

    private CuratorFramework client

    private Integer minPortValue = 10000
    private Integer maxPortValue = 15000
    private String stubRepositoryRoot
    private String stubsGroup
    private String stubsModule
    private boolean skipLocalRepo

    private StubDownloader stubDownloader
    private String definitionSuffix = "-stubs"

    StubRunnerFactory(TestingServer testingServer, ServiceConfigurationResolver serviceConfigurationResolver) {
        this(testingServer, serviceConfigurationResolver, CuratorFrameworkFactory.newClient(testingServer.connectString, RETRY_POLICY), new StubDownloader())
    }

    @PackageScope
    StubRunnerFactory(TestingServer testingServer, ServiceConfigurationResolver serviceConfigurationResolver,
                      CuratorFramework client, StubDownloader stubDownloader) {
        this.testingServer = testingServer
        this.client = client
        this.serviceConfigurationResolver = serviceConfigurationResolver
        this.stubDownloader = stubDownloader
    }

    List<StubRunner> createStubsFromServiceConfiguration() {
        checkArgument(isNotEmpty(stubRepositoryRoot))
        client.start()
        serviceConfigurationResolver.dependencies.collect { String alias, Map dependencyConfig ->
            String dependencyMappingsPath = dependencyConfig[PATH]
            String dependencyInPackageNotation = dependencyMappingsPath.replaceAll("/", ".")
            String groupId = substringBeforeLast(dependencyInPackageNotation, ".")
            String artifactId = substringAfterLast(dependencyInPackageNotation, ".")

            final File unzipedStubDir = stubDownloader.downloadAndUnpackStubJar(skipLocalRepo, stubRepositoryRoot,
                    groupId, artifactId + definitionSuffix)
            final String context = serviceConfigurationResolver.basePath
            return createStubRunner(alias, unzipedStubDir, context, dependencyMappingsPath, minPortValue,
                    maxPortValue, testingServer, client)
        }
    }

    List<StubRunner> createStubsFromStubsModule() {
        checkArgument(isNotEmpty(stubRepositoryRoot))
        checkArgument(isNotEmpty(stubsGroup))
        checkArgument(isNotEmpty(stubsModule))
        client.start()
        final File unzippedStubsDir = stubDownloader.downloadAndUnpackStubJar(skipLocalRepo, stubRepositoryRoot, stubsGroup, stubsModule)
        final String context = serviceConfigurationResolver.basePath
        serviceConfigurationResolver.dependencies.collect { String alias, Map dependencyConfig ->
            return createStubRunner(alias, unzippedStubsDir, context, dependencyConfig[PATH] as String, minPortValue, maxPortValue, testingServer, client)
        }

    }

    private StubRunner createStubRunner(String alias, File unzippedStubsDir, String context, String dependencyMappingsPath, Integer minPortValue, Integer maxPortValue, TestingServer testingServer, CuratorFramework client) {
        List<ProjectMetadata> projects = [new ProjectMetadata(alias, dependencyMappingsPath, context)]
        Arguments arguments = new Arguments(unzippedStubsDir.path, dependencyMappingsPath, testingServer.port, minPortValue, maxPortValue, context, testingServer.connectString, projects)
        return new StubRunner(arguments, new StubRegistry(testingServer.connectString, client))
    }


    StubRunnerFactory withMinPortValue(Integer minPortValue) {
        this.minPortValue = minPortValue
        return this
    }

    StubRunnerFactory withMaxPortValue(Integer maxPortValue) {
        this.maxPortValue = maxPortValue
        return this
    }

    StubRunnerFactory withStubRepositoryRoot(String stubRepositoryRoot) {
        this.stubRepositoryRoot = stubRepositoryRoot
        return this
    }

    StubRunnerFactory withStubsGroup(String stubsGroup) {
        this.stubsGroup = stubsGroup
        return this
    }

    StubRunnerFactory withStubsModule(String stubsModule) {
        this.stubsModule = stubsModule
        return this
    }

    StubRunnerFactory withSkipLocalRepo(boolean skipLocalRepo) {
        this.skipLocalRepo = skipLocalRepo
        return this
    }

    StubRunnerFactory withDefinitionSuffix(String definitionSuffix) {
        this.definitionSuffix = definitionSuffix
        return this
    }
}
