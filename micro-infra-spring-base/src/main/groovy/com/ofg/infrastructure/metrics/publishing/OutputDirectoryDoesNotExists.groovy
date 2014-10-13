package com.ofg.infrastructure.metrics.publishing

import groovy.transform.CompileStatic

@CompileStatic
class OutputDirectoryDoesNotExists extends RuntimeException {
    OutputDirectoryDoesNotExists(File directory) {
        super("$directory.name directory does not exists")
    }
}
