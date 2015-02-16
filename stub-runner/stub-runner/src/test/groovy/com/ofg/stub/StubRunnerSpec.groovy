package com.ofg.stub

import com.ofg.stub.mapping.ProjectMetadata
import com.ofg.stub.registry.StubRegistry
import spock.lang.Specification

class StubRunnerSpec extends Specification {

    private static final int MIN_PORT = 8111
    private static final int MAX_PORT = 8112
    private static final int ZOOKEEPER_PORT = 8113
    private static final URL EXPECTED_STUB_URL = new URL("http://localhost:$MIN_PORT")
    private static final String STUB_RELATIVE_PATH = 'com/ofg/bye'

    private StubRegistry registry = Mock(StubRegistry)

    def 'should provide stub URL for provided relative path'() {
        given:
            StubRunner runner = new StubRunner(argumentsWithProjectDefinition(), registry)
        when:
            runner.runStubs()
        then:
            runner.findStubUrlByRelativePath(STUB_RELATIVE_PATH).get() == EXPECTED_STUB_URL
        cleanup:
            runner.close()
    }

    def argumentsWithProjectDefinition() {
        Collection<ProjectMetadata> projects = [new ProjectMetadata('bye', STUB_RELATIVE_PATH, 'pl')]
        StubRunnerOptions stubRunnerOptions = new StubRunnerOptions(minPortValue: MIN_PORT, maxPortValue: MAX_PORT, zookeeperPort: ZOOKEEPER_PORT)
        return new Arguments(stubRunnerOptions, 'pl', 'src/test/resources/repository', '', projects)
    }

}
