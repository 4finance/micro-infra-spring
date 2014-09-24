package com.ofg.stub.mapping

import spock.lang.Specification

import static com.github.tomakehurst.wiremock.http.RequestMethod.GET

class MappingDescriptorSpec extends Specification {
    public static final File MAPPING_DESCRIPTOR = new File('src/test/resources/repository/mappings/com/ofg/ping/ping.json')

    def 'should describe stub mapping'() {
        given:
            MappingDescriptor mappingDescriptor = new MappingDescriptor(MAPPING_DESCRIPTOR)

        expect:
            with(mappingDescriptor.mapping) {
                request.method == GET
                request.url == '/ping'
                response.status == 200
                response.body == 'pong'
                response.headers.contentTypeHeader.mimeTypePart() == 'text/plain'
            }
    }
}
