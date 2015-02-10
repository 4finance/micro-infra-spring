package com.ofg.stub
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.test.TestingServer
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class StubRunnerFactorySpec extends Specification {

    @Rule
    TemporaryFolder folder = new TemporaryFolder()

    ServiceConfigurationResolver configurationResolver = Stub(ServiceConfigurationResolver)
    TestingServer testingServer = Stub(TestingServer)
    CuratorFramework curatorClient = Stub(CuratorFramework)
    StubDownloader downloader = Mock(StubDownloader)
    StubRunnerFactory factory = new StubRunnerFactory(testingServer, configurationResolver, curatorClient, downloader)

    void setup() {
        factory.withStubRepositoryRoot("http://123.0.0.1/repository")
        testingServer.connectString >> "localhost:12345"
        configurationResolver.dependencies >> [pl: ["path": "com/ofg/risk-service"], eu: ["path": "com/ofg/fraud"]]
    }

    def "Should download stub definitions many times"() {
        given:
            folder.newFolder("/mappings")
            2 * downloader.downloadAndUnpackStubJar(_, _, _, _) >> folder.root
        when:
            List<StubRunner> stubRunners = factory.createStubsFromServiceConfiguration()
        then:
            stubRunners.size() == 2
    }

    def "Should download stub only once"() {
        given:
            folder.newFolder("/mappings")
            factory.withStubsModule("123").withStubsGroup("123")
            1 * downloader.downloadAndUnpackStubJar(_, _, _, _) >> folder.root
        when:
            List<StubRunner> stubRunners = factory.createStubsFromStubsModule()
        then:
            stubRunners.size() == 2


    }
}
