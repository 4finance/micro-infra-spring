package com.ofg.stub.mapping

import spock.lang.Specification

class RepositoryMetadaSpec extends Specification {

    def 'should return true if metadata is null'() {
        given:
            RepositoryMetada repositoryMetada = new RepositoryMetada(null, null)
        expect:
            repositoryMetada.forAllStubs
    }

    def 'should return false if metadata is not null'() {
        given:
            RepositoryMetada repositoryMetada = new RepositoryMetada(null, new File('.'))
        expect:
            !repositoryMetada.forAllStubs
    }
}
