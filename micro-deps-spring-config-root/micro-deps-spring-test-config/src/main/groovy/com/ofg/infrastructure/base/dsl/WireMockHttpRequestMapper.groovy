package com.ofg.infrastructure.base.dsl

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import groovy.transform.CompileStatic

/**
 * A class that contains static helper methods that map HTTP methods with given path 
 */
@CompileStatic
class WireMockHttpRequestMapper {

    static MappingBuilder wireMockGet(String path) {
        return WireMock.get(WireMock.urlEqualTo(path))
    }
    
    static MappingBuilder wireMockPut(String path) {
        return WireMock.put(WireMock.urlEqualTo(path))
    }
    
    static MappingBuilder wireMockPost(String path) {
        return WireMock.post(WireMock.urlEqualTo(path))
    }
    
    static MappingBuilder wireMockDelete(String path) {
        return WireMock.delete(WireMock.urlEqualTo(path))
    }
    
    static MappingBuilder wireMockOptions(String path) {
        return WireMock.options(WireMock.urlEqualTo(path))
    }
    
    static MappingBuilder wireMockHead(String path) {
        return WireMock.head(WireMock.urlEqualTo(path))
    }
    
}
