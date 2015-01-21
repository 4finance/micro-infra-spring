package com.ofg.stub

import com.google.common.base.Optional
import spock.lang.Specification

class BatchStubRunnerSpec extends Specification {

    static final String KNOWN_STUB_PATH = 'com/ofg/ping'
    static final String UNKNOWN_STUB_PATH = 'com/ofg/unknown'
    static final URL KNOWN_STUB_URL = new URL('http://localhost:8080')

    def 'should provide stub URL from enclosed stub runner'() {
        given:
            BatchStubRunner batchStubRunner = new BatchStubRunner(runners())
        expect:
            batchStubRunner.findStubUrlByRelativePath(KNOWN_STUB_PATH).get() == KNOWN_STUB_URL
    }

    def 'should return empty optional for unknown stub path'() {
        given:
            BatchStubRunner batchStubRunner = new BatchStubRunner(runners())
        expect:
            !batchStubRunner.findStubUrlByRelativePath(UNKNOWN_STUB_PATH).present
    }

    List<StubRunner> runners() {
        StubRunner runner = Mock(StubRunner)
        runner.findStubUrlByRelativePath(KNOWN_STUB_PATH) >> Optional.of(KNOWN_STUB_URL)
        runner.findStubUrlByRelativePath(UNKNOWN_STUB_PATH) >> Optional.absent()
        return [runner]
    }

}
