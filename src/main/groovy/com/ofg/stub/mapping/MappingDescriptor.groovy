package com.ofg.stub.mapping

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@CompileStatic
@EqualsAndHashCode
@ToString
class MappingDescriptor {
    final File descriptor
    final MappingType mappingType

    MappingDescriptor(File mappingDescriptor) {
        this.descriptor = mappingDescriptor
        this.mappingType = MappingType.GLOBAL
    }

    MappingDescriptor(File mappingDescriptor, MappingType mappingType) {
        this.descriptor = mappingDescriptor
        this.mappingType = mappingType
    }


    StubMapping getMapping() {
        return StubMapping.buildFrom(descriptor.text)
    }

    static enum MappingType {
        GLOBAL, REALM_SPECIFIC
    }
}
