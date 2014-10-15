package com.ofg.stub

import groovy.transform.CompileStatic

/**
 * Manages lifecycle of multiple {@link StubRunner} instances.
 *
 * * @see StubRunner
 */
@CompileStatic
class BatchStubRunner implements StubRunning, Closeable {

    private final List<StubRunner> stubRunners

    BatchStubRunner(List<StubRunner> stubRunners) {
        this.stubRunners = stubRunners
    }

    @Override
    void runStubs() {
        stubRunners*.runStubs()
    }

    @Override
    void close() throws IOException {
        stubRunners*.close()
    }
}
