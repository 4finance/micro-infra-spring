package com.ofg.infrastructure.correlationid

import groovy.transform.CompileStatic

@CompileStatic
class UuidGenerator {

    String create() {
        return UUID.randomUUID().toString()
    }

}
