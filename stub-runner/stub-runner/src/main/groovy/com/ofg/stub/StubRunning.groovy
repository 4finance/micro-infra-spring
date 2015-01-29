package com.ofg.stub

import com.google.common.base.Optional

interface StubRunning extends Closeable {
    void runStubs()
    Optional<URL> findStubUrlByRelativePath(String relativePath)
}