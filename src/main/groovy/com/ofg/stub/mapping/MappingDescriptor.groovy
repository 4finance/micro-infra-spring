package com.ofg.stub.mapping

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import groovy.transform.EqualsAndHashCode
import groovy.transform.TypeChecked

@TypeChecked
@EqualsAndHashCode
class MappingDescriptor {
    final File descriptor

    MappingDescriptor(File mappingDescriptor) {
        descriptor = mappingDescriptor
    }

    StubMapping getMapping() {
        return StubMapping.buildFrom(descriptor.text)
    }
}
