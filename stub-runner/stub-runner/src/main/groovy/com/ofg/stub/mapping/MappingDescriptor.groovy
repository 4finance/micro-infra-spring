package com.ofg.stub.mapping

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import static com.ofg.stub.mapping.MappingDescriptor.MappingType.GLOBAL

@CompileStatic
@EqualsAndHashCode
@ToString
class MappingDescriptor {
    final File descriptor
    final MappingType mappingType

    MappingDescriptor(File mappingDescriptor) {
        this.descriptor = mappingDescriptor
        this.mappingType = GLOBAL
    }

    MappingDescriptor(File mappingDescriptor, MappingType mappingType) {
        this.descriptor = mappingDescriptor
        this.mappingType = mappingType
    }

    StubMapping getMapping() {
        return StubMapping.buildFrom(descriptor.getText('UTF-8'))
    }

    static enum MappingType {
        GLOBAL, REALM_SPECIFIC
    }
}
