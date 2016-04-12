package com.ofg.infrastructure.web.correlationid
import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.ConfigurationWithoutServiceDiscovery
import com.ofg.infrastructure.base.MvcCorrelationIdSettingIntegrationSpec
import org.slf4j.MDC
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Ignore

import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER
import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.OLD_CORRELATION_ID_HEADER

@ContextConfiguration(classes = [BaseConfiguration, ConfigurationWithoutServiceDiscovery], loader = SpringApplicationContextLoader)
class CorrelationIdFilterSpec extends MvcCorrelationIdSettingIntegrationSpec {

    private Random random = new Random();

    def "should create and return correlationId in HTTP header"() {
        when:
            MvcResult mvcResult = sendPingWithoutCorrelationId()

        then:
            getCorrelationIdFromResponseHeader(mvcResult) != null
    }

    def "when correlationId is sent, should not create a new one, but return the existing one instead"() {
        given:
            String passedCorrelationId = random.nextLong()

        when:
            MvcResult mvcResult = sendPingWithCorrelationId(passedCorrelationId)

        then:
            getCorrelationIdFromResponseHeader(mvcResult) == passedCorrelationId
    }

    def "when the old correlationId is sent, should not create a new one, but return the existing one instead"() {
        given:
            String passedCorrelationId = random.nextLong()

        when:
            MvcResult mvcResult = sendPingWithOldCorrelationId(passedCorrelationId)

        then:
            getCorrelationIdFromResponseHeader(mvcResult) == passedCorrelationId
    }

    @Ignore("With spans the approach is different")
    def "should clean up MDC after the call"() {
        given:
            String passedCorrelationId = random.nextLong()

        when:
            sendPingWithCorrelationId(passedCorrelationId)

        then:
            MDC.get(CORRELATION_ID_HEADER) == null
    }

    private MvcResult sendPingWithCorrelationId(String passedCorrelationId) {
        sendPingWithCorrelationId(CORRELATION_ID_HEADER, passedCorrelationId)
    }

    private MvcResult sendPingWithOldCorrelationId(String passedCorrelationId) {
        sendPingWithCorrelationId(OLD_CORRELATION_ID_HEADER, passedCorrelationId)
    }

    private MvcResult sendPingWithCorrelationId(String headerName, String passedCorrelationId) {
        mockMvc.perform(MockMvcRequestBuilders.get('/ping').accept(MediaType.TEXT_PLAIN)
                .header(headerName, passedCorrelationId)).andReturn()
    }

    private MvcResult sendPingWithoutCorrelationId() {
        mockMvc.perform(MockMvcRequestBuilders.get('/ping').accept(MediaType.TEXT_PLAIN)).andReturn()
    }

    private String getCorrelationIdFromResponseHeader(MvcResult mvcResult) {
        mvcResult.response.getHeader(CORRELATION_ID_HEADER)
    }
}
