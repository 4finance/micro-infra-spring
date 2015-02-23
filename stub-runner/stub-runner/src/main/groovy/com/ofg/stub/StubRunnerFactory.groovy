package com.ofg.stub
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
    private final Collaborators collaborators
    private final StubDownloader stubDownloader
    private final CuratorFramework client

    StubRunnerFactory(StubRunnerOptions stubRunnerOptions, Collaborators collaborators) {
        this(stubRunnerOptions, collaborators, CuratorFrameworkFactory.newClient(stubRunnerOptions.zookeeperConnectString, RETRY_POLICY), new StubDownloader())
    }

    @PackageScope
    StubRunnerFactory(StubRunnerOptions stubRunnerOptions, Collaborators collaborators,
                      CuratorFramework client, StubDownloader stubDownloader) {
        this.stubRunnerOptions = stubRunnerOptions
        this.client = client
        this.collaborators = collaborators
        this.stubDownloader = stubDownloader
    }

    List<StubRunner> createStubsFromServiceConfiguration() {
        checkArgument(isNotEmpty(stubRunnerOptions.stubRepositoryRoot))
        client.start()
        return collaborators.collaboratorsPath.collect { String dependencyMappingsPath ->
            Module module = new Module(dependencyMappingsPath)
            final File unzipedStubDir = stubDownloader.downloadAndUnpackStubJar(stubRunnerOptions.skipLocalRepo, stubRunnerOptions.stubRepositoryRoot,
                    module.groupId, "$module.artifactId${getStubDefinitionSuffix()}")
            final String context = collaborators.basePath
            return createStubRunner(module.artifactId, unzipedStubDir, context, dependencyMappingsPath, stubRunnerOptions, client)
        }
    }

    private String getStubDefinitionSuffix() {
        return stubRunnerOptions.stubDefinitionSuffix ? "-${stubRunnerOptions.stubDefinitionSuffix}" : ""
    }

    List<StubRunner> createStubsFromStubsModule() {
        checkArgument(isNotEmpty(stubRunnerOptions.stubRepositoryRoot))
        checkArgument(isNotEmpty(stubRunnerOptions.stubsGroup))
        checkArgument(isNotEmpty(stubRunnerOptions.stubsModule))
        client.start()
        final File unzippedStubsDir = stubDownloader.downloadAndUnpackStubJar(stubRunnerOptions.skipLocalRepo, stubRunnerOptions.stubRepositoryRoot, stubRunnerOptions.stubsGroup, stubRunnerOptions.stubsModule)
        final String context = collaborators.basePath
        return collaborators.collaboratorsPath.collect { String dependencyMappingsPath ->
            Module module = new Module(dependencyMappingsPath)
            return createStubRunner(module.artifactId, unzippedStubsDir, context, dependencyMappingsPath as String, stubRunnerOptions, client)
        }
    }

    private StubRunner createStubRunner(String alias, File unzippedStubsDir, String context, String dependencyMappingsPath, StubRunnerOptions stubRunnerOptions, CuratorFramework client) {
        List<ProjectMetadata> projects = [new ProjectMetadata(alias, dependencyMappingsPath, context)]
        Arguments arguments = new Arguments(stubRunnerOptions, context, unzippedStubsDir.path, alias, projects)
        return new StubRunner(arguments, new StubRegistry(stubRunnerOptions.zookeeperConnectString, client))
    }

    private class Module {
        final String groupId
        final String artifactId

        Module(String dependencyMappingsPath) {
            String dependencyInPackageNotation = dependencyMappingsPath.replaceAll("/", ".")
            groupId = substringBeforeLast(dependencyInPackageNotation, ".")
            artifactId = substringAfterLast(dependencyInPackageNotation, ".")
        }

    }

}
