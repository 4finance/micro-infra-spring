package com.ofg.infrastructure.correlationid

import groovyx.gpars.GParsPool
import spock.lang.Ignore
import spock.lang.Specification

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import static java.util.concurrent.TimeUnit.SECONDS


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

    def "correlation ID should not be propagated to other thread by default"() {
        given:
            CorrelationIdUpdater.updateCorrelationId('A')
        expect:
            GParsPool.withPool(1) {
                ["1"].eachParallel {
                    CorrelationIdHolder.get() == null
                }
            }
    }

    def "should propagate correlation ID to closure execution in other thread with GPars"() {
        given:
            CorrelationIdUpdater.updateCorrelationId('A')
        expect:
            GParsPool.withPool(1) {
                ["1"].eachParallel CorrelationIdUpdater.wrapClosureWithId {
                    CorrelationIdHolder.get() == 'A'
                }
            }
    }

    def "should restore original correlation ID after closure execution in other thread"() {
        given:
            CorrelationIdUpdater.updateCorrelationId('A')
        expect:
            GParsPool.withPool(1) {
                //given
                ["1"].eachParallel {
                    CorrelationIdHolder.set('B')
                }
                //when
                ["1"].eachParallel CorrelationIdUpdater.wrapClosureWithId {}
                //then
                ["1"].eachParallel {
                    CorrelationIdHolder.get() == 'B'
                }
            }
    }

    def "should propagate single parameter into nested closure"() {
        expect:
            GParsPool.withPool {
                ["1"].eachParallel CorrelationIdUpdater.wrapClosureWithId {
                    it == "1"
                }
            }
    }

    def "should propagate single named parameter into nested closure"() {
        expect:
            ["1"].each CorrelationIdUpdater.wrapClosureWithId { String elem ->
                elem == "1"
            }
    }

    @Ignore
    def "should propagate multiple parameters into nested closure"() {
        expect:
            ["e1"].eachWithIndex CorrelationIdUpdater.wrapClosureWithId { String entry, int i ->
                entry == "e1"
                i == 0
            }
    }

    def "should propagate correlation ID into nested Callable"() {
        given:
            ExecutorService threadPool = Executors.newFixedThreadPool(1)
            CorrelationIdUpdater.updateCorrelationId('A')
            Callable<String> callable = new CorrelationIdTestCallable()
        when:
            Callable<String> wrappedCallable = CorrelationIdUpdater.wrapCallableWithId(callable)
            String nestedCorrelationId = threadPool.submit(wrappedCallable).get(1, SECONDS)
        then:
            nestedCorrelationId == 'A'
        cleanup:
            threadPool.shutdown()
    }

    def "should restore previous correlation ID after Callable execution in other thread"() {
        given:
            ExecutorService threadPool = Executors.newFixedThreadPool(1)
            CorrelationIdUpdater.updateCorrelationId('A')
            Callable<String> callable = new CorrelationIdTestCallable()
        and:
            threadPool.submit({ CorrelationIdHolder.set('B') }).get(1, SECONDS)
        when:
            threadPool.submit(CorrelationIdUpdater.wrapCallableWithId(callable)).get(1, SECONDS)
        then:
            def restoredCorrelationId = threadPool.submit({ CorrelationIdHolder.get() } as Callable).get(1, SECONDS)
            restoredCorrelationId == 'B'
        cleanup:
            threadPool.shutdown()
    }

    //Explicit plain old Callable class instead of `{} as Callable`
    private static class CorrelationIdTestCallable implements Callable<String> {
        @Override
        String call() throws Exception {
            CorrelationIdHolder.get()
        }
    }
}
