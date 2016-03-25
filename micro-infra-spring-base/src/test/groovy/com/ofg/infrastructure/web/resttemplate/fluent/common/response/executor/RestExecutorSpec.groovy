package com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor

import com.nurkiewicz.asyncretry.SyncRetryExecutor
import com.ofg.infrastructure.web.resttemplate.custom.RestTemplate
import com.ofg.infrastructure.web.resttemplate.fluent.FakeTrace
import org.springframework.cloud.sleuth.Tracer
import org.springframework.http.HttpMethod
import org.springframework.web.client.ResourceAccessException
import spock.lang.Specification

class RestExecutorSpec extends Specification {

    Tracer trace = new FakeTrace()

    def "should fail to run asynchronously if retry mechanism wasn't set up"() {
        given:
            RestExecutor executor = new RestExecutor(
                    new RestTemplate(), SyncRetryExecutor.INSTANCE, trace)
        when:
            executor.exchangeAsync(HttpMethod.PUT, [:], Object)

        then:
            IllegalStateException e = thrown(IllegalStateException)
            e.message.contains("retryUsing")
    }

    def 'should propagate original RestTemplate exception'() {
        given:
            RestExecutor executor = new RestExecutor(
                    new RestTemplate(), SyncRetryExecutor.INSTANCE, trace)
        when:
            executor.exchange(HttpMethod.GET, [host: { 'http://localhost:7777' }, url: '/api'.toURI()], Object)

        then:
            ResourceAccessException e = thrown(ResourceAccessException)
            e.message.contains('localhost:7777')
    }

}
