package com.ofg.infrastructure.discovery.web

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.Options
import groovy.transform.CompileStatic

/**
 * Custom implementation of {@link WireMockServer} that by default registers itself at port 
 * {@link HttpMockServer#DEFAULT_PORT}.
 *
 * @see WireMockServer
 */
@CompileStatic
class HttpMockServer extends WireMockServer {

    public static final int DEFAULT_PORT = 8030

    HttpMockServer(int port) {
        super(port)
    }

    HttpMockServer() {
        super(DEFAULT_PORT)
    }

    HttpMockServer(Options options) {
        super(options)
    }

    void shutdownServer() {
        if (isRunning()) {
            stop()
        }
        shutdown()
    }
}