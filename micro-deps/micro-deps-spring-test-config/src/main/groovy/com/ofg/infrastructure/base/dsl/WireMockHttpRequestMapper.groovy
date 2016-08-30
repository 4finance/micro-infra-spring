package com.ofg.infrastructure.base.dsl

import com.github.tomakehurst.wiremock.client.RemoteMappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import groovy.transform.CompileStatic

/**
 * A class that contains static helper methods that map HTTP methods with given path 
 */
@CompileStatic
class WireMockHttpRequestMapper {

    static RemoteMappingBuilder wireMockGet(String path) {
        return WireMock.get(WireMock.urlEqualTo(path))
    }

    static RemoteMappingBuilder wireMockPut(String path) {
        return WireMock.put(WireMock.urlEqualTo(path))
    }

    static RemoteMappingBuilder wireMockPost(String path) {
        return WireMock.post(WireMock.urlEqualTo(path))
    }

    static RemoteMappingBuilder wireMockDelete(String path) {
        return WireMock.delete(WireMock.urlEqualTo(path))
    }

    static RemoteMappingBuilder wireMockOptions(String path) {
        return WireMock.options(WireMock.urlEqualTo(path))
    }

    static RemoteMappingBuilder wireMockHead(String path) {
        return WireMock.head(WireMock.urlEqualTo(path))
    }
    
}
