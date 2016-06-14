package com.ofg.infrastructure.web.logging

import spock.lang.Specification

import static com.jayway.awaitility.Awaitility.await
import static com.ofg.infrastructure.web.logging.HttpDataFactory.createHttpData
import static feign.Response.create
import static java.nio.charset.Charset.defaultCharset
import static java.util.concurrent.TimeUnit.MILLISECONDS

class RequestDataProviderTest extends Specification {

    private final static int TIME_TO_LIVE_MILLIS = 500;
    
    def 'Should delete httpData after specified time to live'() {
        given:
            RequestDataProvider provider = new RequestDataProvider(TIME_TO_LIVE_MILLIS);
        when:
            provider.store("1", httpData());
        then:
            provider.retrieve("1") != null
            await().atMost(TIME_TO_LIVE_MILLIS + 500, MILLISECONDS).until({
                assert provider.retrieve("1") == null    
            })
    }

    private static HttpData httpData() {
        createHttpData(create(200, '', [:], 'content', defaultCharset()))
    }

}
