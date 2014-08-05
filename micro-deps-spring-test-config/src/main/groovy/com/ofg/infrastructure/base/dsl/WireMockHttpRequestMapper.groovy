package com.ofg.infrastructure.base.dsl
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import groovy.transform.TypeChecked

//TODO: this needs a usage example (preferably as tests)
@TypeChecked
class WireMockHttpRequestMapper {

    static MappingBuilder wireMockGet(String path) {
        return WireMock.get(WireMock.urlEqualTo(path))
    }
    
    static MappingBuilder wireMockPut(String path) {
        return WireMock.put(WireMock.urlEqualTo(path))
    }
    
}
