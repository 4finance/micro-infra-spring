package com.ofg.stub
import com.google.common.base.Optional
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

    private List<StubRunner> collectOnlyPresentValues(List<Optional<StubRunner>> stubRunners) {
        return stubRunners.findAll { it.present }.collect { it.get() }
    }
}
