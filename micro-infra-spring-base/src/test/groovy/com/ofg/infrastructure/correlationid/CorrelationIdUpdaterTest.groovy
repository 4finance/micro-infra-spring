package com.ofg.infrastructure.correlationid

import groovyx.gpars.GParsPool
import spock.lang.Ignore
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

    def "should propagate correlation ID to closure execution in other thread with GPars2"() {
        given:
            CorrelationIdUpdater.updateCorrelationId('A')
        expect:
            GParsPool.withPool {
                ["1"].eachParallel CorrelationIdUpdater.closureWithId {
                    CorrelationIdHolder.get() == 'A'
                }
            }
    }

    def "should propagate single parameter into nested closure"() {
        expect:
            GParsPool.withPool {
                ["1"].eachParallel CorrelationIdUpdater.closureWithId {
                    it == "1"
                }
            }
    }

    def "should propagate single named parameter into nested closure"() {
        expect:
            ["1"].each CorrelationIdUpdater.closureWithId { String elem ->
                elem == "1"
            }
    }

    @Ignore
    def "should propagate multiple parameters into nested closure"() {
        expect:
            ["e1"].eachWithIndex CorrelationIdUpdater.closureWithId { String entry, int i ->
                entry == "e1"
                i == 0
            }
    }
}
