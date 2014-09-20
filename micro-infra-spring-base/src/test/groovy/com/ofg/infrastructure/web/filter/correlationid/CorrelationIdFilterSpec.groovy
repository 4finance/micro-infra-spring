package com.ofg.infrastructure.web.filter.correlationid

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.ConfigurationWithoutServiceDiscovery
import com.ofg.infrastructure.base.MvcIntegrationSpec
import org.slf4j.MDC
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

import static com.ofg.infrastructure.web.filter.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER

@ContextConfiguration(classes = [BaseConfiguration, ConfigurationWithoutServiceDiscovery], loader = SpringApplicationContextLoader)
class CorrelationIdFilterSpec extends MvcIntegrationSpec {

    def "should create and return correlationId in HTTP header"() {
        when:
            MvcResult mvcResult = sendPingWithoutCorrelationId()

        then:
            getCorrelationIdFromResponseHeader(mvcResult) != null
    }

    def "when correlationId is sent, should not create a new one, but return the existing one instead"() {
        given:
            String passedCorrelationId = "passedCorId"

        when:
            MvcResult mvcResult = sendPingWithCorrelationId(passedCorrelationId)

        then:
            getCorrelationIdFromResponseHeader(mvcResult) == passedCorrelationId
    }

    def "should clean up MDC after the call"() {
        given:
            String passedCorrelationId = "passedCorId"

        when:
            sendPingWithCorrelationId(passedCorrelationId)

        then:
            MDC.get(CORRELATION_ID_HEADER) == null
    }

    private MvcResult sendPingWithCorrelationId(String passedCorrelationId) {
        mockMvc.perform(MockMvcRequestBuilders.get('/ping').accept(MediaType.TEXT_PLAIN)
                .header(CORRELATION_ID_HEADER, passedCorrelationId)).andReturn()
    }

    private MvcResult sendPingWithoutCorrelationId() {
        mockMvc.perform(MockMvcRequestBuilders.get('/ping').accept(MediaType.TEXT_PLAIN)).andReturn()
    }

    private String getCorrelationIdFromResponseHeader(MvcResult mvcResult) {
        mvcResult.response.getHeader(CORRELATION_ID_HEADER)
    }
}
