package com.ofg.infrastructure.correlationid

import spock.lang.Specification


class CorrelationIdUpdaterTest extends Specification {

    def cleanup() {
        CorrelationIdHolder.remove()
    }

    def 'should temporarily switch correlation ID in closure'() {
        given:
            CorrelationIdUpdater.updateCorrelationId('A')

        when:
            String idInClosure = CorrelationIdUpdater.withId('B') {
                return CorrelationIdHolder.get()
            }

        then:
            idInClosure == 'B'
    }

    def 'should restore old correlation ID after running closure with temp one'() {
        given:
            CorrelationIdUpdater.updateCorrelationId('A')

        when:
            CorrelationIdUpdater.withId('B') {}

        then:
            CorrelationIdHolder.get() == 'A'
    }

}
