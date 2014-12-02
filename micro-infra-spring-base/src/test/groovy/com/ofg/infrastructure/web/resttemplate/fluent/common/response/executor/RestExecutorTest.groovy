package com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor

import com.nurkiewicz.asyncretry.SyncRetryExecutor
import com.ofg.infrastructure.web.resttemplate.custom.RestTemplate
import org.springframework.http.HttpMethod
import spock.lang.Specification

class RestExecutorTest extends Specification {

    def "should fail to run asynchronously if retry mechanism wasn't set up"() {
        given:
            RestExecutor executor = new RestExecutor(
                    new RestTemplate(), SyncRetryExecutor.INSTANCE)
        when:
            executor.exchangeAsync(HttpMethod.PUT, [:], Object)

        then:
            IllegalStateException e = thrown(IllegalStateException)
            e.message.contains("retryUsing")
    }

}
