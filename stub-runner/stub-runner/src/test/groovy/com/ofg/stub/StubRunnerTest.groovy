package com.ofg.stub

import com.ofg.stub.registry.StubRegistry
import spock.lang.Specification

class StubRunnerTest extends Specification {

    private static final int MIN_PORT = 8111
    private static final int MAX_PORT = 8112
    private static final int ZOOKEEPER_PORT = 8113
    private static final URL EXPECTED_STUB_URL = new URL("http://localhost:$MIN_PORT")

    private StubRegistry registry = Mock(StubRegistry)

    def 'should provide stub URL for provided relative path'() {
        given:
            Arguments args = new Arguments('src/test/resources/repository', 'bye_metadata.json', ZOOKEEPER_PORT, MIN_PORT, MAX_PORT, '', '')
            String relativePath = 'com/ofg/bye'
            StubRunner runner = new StubRunner(args, registry)
        when:
            runner.runStubs()
        then:
            runner.findStubUrlByRelativePath(relativePath).get() == EXPECTED_STUB_URL
        cleanup:
            runner.close()
    }

}
