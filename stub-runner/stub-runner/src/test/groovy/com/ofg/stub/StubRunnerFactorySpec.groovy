package com.ofg.stub

import org.apache.curator.framework.CuratorFramework
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class StubRunnerFactorySpec extends Specification {

    @Rule
    TemporaryFolder folder = new TemporaryFolder()

    Collaborators collaborators = new Collaborators('pl', ["com/ofg/risk-service", "com/ofg/fraud"])
    CuratorFramework curatorFramework = Stub()
    StubDownloader downloader = Mock(StubDownloader)
    String connectString = "localhost:12345"
    StubRunnerOptions stubRunnerOptions = new StubRunnerOptions(zookeeperConnectString: connectString, stubRepositoryRoot: 'pl')
    StubRunnerFactory factory = new StubRunnerFactory(stubRunnerOptions, collaborators, curatorFramework, downloader)

    def "Should download stub definitions many times"() {
        given:
            folder.newFolder("/mappings")
            2 * downloader.downloadAndUnpackStubJar(_, _, _, _) >> folder.root
            stubRunnerOptions.stubRepositoryRoot = folder.root.absolutePath
        when:
            List<StubRunner> stubRunners = factory.createStubsFromServiceConfiguration()
        then:
            stubRunners.size() == 2
    }

    def "Should download stub only once"() {
        given:
            folder.newFolder("/mappings")
            stubRunnerOptions.stubsModule = "123"
            stubRunnerOptions.stubsGroup = "123"
            1 * downloader.downloadAndUnpackStubJar(_, _, _, _) >> folder.root
            stubRunnerOptions.stubRepositoryRoot = folder.root.absolutePath
        when:
            List<StubRunner> stubRunners = factory.createStubsFromStubsModule()
        then:
            stubRunners.size() == 2


    }
}
