package com.ofg.infrastructure.base.dsl

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import groovy.transform.CompileStatic

//TODO: this needs a usage example (preferably as tests)
@CompileStatic
class WireMockHttpRequestMapper {

    static MappingBuilder wireMockGet(String path) {
        return WireMock.get(WireMock.urlEqualTo(path))
    }
    
    static MappingBuilder wireMockPut(String path) {
        return WireMock.put(WireMock.urlEqualTo(path))
    }
    
}
