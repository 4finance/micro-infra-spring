package com.ofg.stub

import com.google.common.base.Optional
import org.apache.curator.framework.CuratorFramework
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static com.ofg.infrastructure.discovery.MicroserviceConfiguration.Dependency.StubsConfiguration

class StubRunnerFactorySpec extends Specification {

    @Rule
    TemporaryFolder folder = new TemporaryFolder()

    Collaborators collaborators = new Collaborators('pl', ["com/ofg/risk-service", "com/ofg/fraud"], ['': null])
    CuratorFramework curatorFramework = Stub(CuratorFramework)
    StubDownloader downloader = Mock(StubDownloader)
    String connectString = "localhost:12345"
    StubRunnerOptions stubRunnerOptions = new StubRunnerOptions(zookeeperConnectString: connectString, stubRepositoryRoot: 'pl')
    StubRunnerFactory factory = new StubRunnerFactory(stubRunnerOptions, collaborators, curatorFramework, downloader)

    def "Should download stub definitions many times"() {
        given:
            folder.newFolder("mappings")
            2 * downloader.downloadAndUnpackStubJar(_, _, _) >> folder.root
            stubRunnerOptions.stubRepositoryRoot = folder.root.absolutePath
        when:
            List<StubRunner> stubRunners = collectOnlyPresentValues(factory.createStubsFromServiceConfiguration())
        then:
            stubRunners.size() == 2
    }

    def "Should download stub only once"() {
        given:
            folder.newFolder("mappings")
            stubRunnerOptions.stubsModule = "123"
            stubRunnerOptions.stubsGroup = "123"
            1 * downloader.downloadAndUnpackStubJar(_, _, _) >> folder.root
            stubRunnerOptions.stubRepositoryRoot = folder.root.absolutePath
        when:
            List<StubRunner> stubRunners = collectOnlyPresentValues(factory.createStubsFromStubsModule())
        then:
            stubRunners.size() == 2
    }

    def "should not create a stub runner if we couldn't download a stub"() {
        given:
            stubRunnerOptions.stubsModule = "123"
            stubRunnerOptions.stubsGroup = "123"
            stubRunnerOptions.stubRepositoryRoot = folder.root.absolutePath
            downloader.downloadAndUnpackStubJar(_, _, _) >> null
        when:
            List<StubRunner> stubRunners = collectOnlyPresentValues(factory.createStubsFromStubsModule())
        then:
            stubRunners.empty
    }

    def "should download stubs from StubsConfiguration and from dependencyMappingPath"() {
        given:
            Collaborators collaborators = new Collaborators('pl', ['com/ofg/risk-service', 'com/ofg/fraud'],
                    ['com/ofg/risk-service': new StubsConfiguration('com.ofg', 'custom-risk-service', 'stub')])
            StubRunnerFactory factory = new StubRunnerFactory(stubRunnerOptions, collaborators, curatorFramework, downloader)
        when:
            factory.createStubsFromServiceConfiguration()
        then:
            1 * downloader.downloadAndUnpackStubJar("com.ofg", "custom-risk-service", "stub")
            1 * downloader.downloadAndUnpackStubJar('com.ofg', 'fraud', 'stubs')
            1 * downloader.downloadAndUnpackStubJar('com.ofg', 'fraud', null)
    }

    def "should create stubs with service correct name from dependencyMappingPath and unzipped folder from StubsConfiguration"() {
        given:
            folder.newFolder("mappings")
            Collaborators collaborators = new Collaborators('pl', ['com/ofg/risk-service'],
                    ['com/ofg/risk-service': new StubsConfiguration('com.ofg', 'different-name-for-stubs', 'stub')])
            StubRunnerFactory factory = new StubRunnerFactory(stubRunnerOptions, collaborators, curatorFramework, downloader)
        when:
            List<Optional<StubRunner>>  listOfOptionals = factory.createStubsFromServiceConfiguration()
        then:
            1 * downloader.downloadAndUnpackStubJar("com.ofg", "different-name-for-stubs", "stub") >> folder.root
            listOfOptionals.get(0).get().arguments.serviceName == 'risk-service'
    }

    def "should try to download using stuboptions.classifier properties if no dependency found"() {
        given:
            folder.newFolder("mappings")
            Collaborators collaborators = new Collaborators('pl', ["com/ofg/risk-service", "com/ofg/fraud"],
                    ['com/ofg/risk-service': new StubsConfiguration('com.ofg', 'risk-service', '')])
            stubRunnerOptions.stubClassifier = 'anyClassifier'
            StubRunnerFactory factory = new StubRunnerFactory(stubRunnerOptions, collaborators, curatorFramework, downloader)
        when:
            factory.createStubsFromServiceConfiguration()
        then:
            1 * downloader.downloadAndUnpackStubJar('com.ofg', 'risk-service', 'anyClassifier') >> folder.root
    }

    def "should not add stuboptions.stubDefinitionSuffix if stubs set"() {
        given:
            folder.newFolder("mappings")
            Collaborators collaborators = new Collaborators('pl', ["com/ofg/risk-service"],
                    ['com/ofg/risk-service': new StubsConfiguration('com.ofg', 'risk-service', '')])
            stubRunnerOptions.stubClassifier = 'anyClassifier'
            stubRunnerOptions.stubDefinitionSuffix = 'stubs'
            StubRunnerFactory factory = new StubRunnerFactory(stubRunnerOptions, collaborators, curatorFramework, downloader)
        when:
            factory.createStubsFromServiceConfiguration()
        then:
            1 * downloader.downloadAndUnpackStubJar('com.ofg', 'risk-service', 'anyClassifier') >> folder.root
    }


    def "should add stuboptions.stubDefinitionSuffix if property set and no stubs"() {
        given:
            folder.newFolder("mappings")
            Collaborators collaborators = new Collaborators('pl', ["com/ofg/risk-service"], [:])
            stubRunnerOptions.stubClassifier = 'anyClassifier'
            stubRunnerOptions.stubDefinitionSuffix = 'stubs'
            StubRunnerFactory factory = new StubRunnerFactory(stubRunnerOptions, collaborators, curatorFramework, downloader)
        when:
            factory.createStubsFromServiceConfiguration()
        then:
            1 * downloader.downloadAndUnpackStubJar('com.ofg', 'risk-service-stubs', 'anyClassifier') >> folder.root
    }

    def "should not stuboptions.stubDefinitionSuffix if property empty and no stubs"() {
        given:
            folder.newFolder("mappings")
            Collaborators collaborators = new Collaborators('pl', ["com/ofg/risk-service"], [:])
            stubRunnerOptions.stubClassifier = 'anyClassifier'
            stubRunnerOptions.stubDefinitionSuffix = ''
            StubRunnerFactory factory = new StubRunnerFactory(stubRunnerOptions, collaborators, curatorFramework, downloader)
        when:
            factory.createStubsFromServiceConfiguration()
        then:
            1 * downloader.downloadAndUnpackStubJar('com.ofg', 'risk-service', 'anyClassifier') >> folder.root
    }


    private List<StubRunner> collectOnlyPresentValues(List<Optional<StubRunner>> stubRunners) {
        return stubRunners.findAll { it.present }.collect { it.get() }
    }
}
