package com.ofg.stub.mapping

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@EqualsAndHashCode
class RepositoryMetada {
    public static final String EMPTY_CONTEXT = ''
    final File repositoryPath
    final File metadata
    final String context

    RepositoryMetada(File repositoryPath, File metadata, String context) {
        this.repositoryPath = repositoryPath
        this.metadata = metadata
        this.context = context
    }

    RepositoryMetada(File repositoryPath, File metadata) {
        this.repositoryPath = repositoryPath
        this.metadata = metadata
        this.context = EMPTY_CONTEXT
    }

    boolean isForAllStubs() {
        return metadata == null
    }

    boolean isContextProvided() {
        return context
    }
}
