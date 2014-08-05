package com.ofg.infrastructure.web.filter.correlationid

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MvcIntegrationSpec
import com.ofg.infrastructure.web.config.WebConfiguration
import org.slf4j.MDC
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

@ContextConfiguration(classes = [BaseConfiguration, WebConfiguration], loader = SpringApplicationContextLoader)
class CorrelationIdFilterSpec extends MvcIntegrationSpec {

    def "should create and return correlationId in HTTP header"() {
        when:
            MvcResult mvcResult = sendPingWithoutCorrelationId()

        then:
            getCorrelationIdFromResonseHeader(mvcResult) != null
    }

    def "when correlationId is sent, should not create a new one, but return the existing one instead"() {
        given:
            String passedCorrelationId = "passedCorId"

        when:
            MvcResult mvcResult = sendPingWithCorrelationId(passedCorrelationId)

        then:
            getCorrelationIdFromResonseHeader(mvcResult) == passedCorrelationId
    }

    def "should clean up MDC after the call"() {
        given:
            String passedCorrelationId = "passedCorId"

        when:
            sendPingWithCorrelationId(passedCorrelationId)

        then:
            MDC.get(CorrelationIdHolder.CORRELATION_ID_HEADER) == null
    }

    private MvcResult sendPingWithCorrelationId(String passedCorrelationId) {
        mockMvc.perform(MockMvcRequestBuilders.get('/ping').accept(MediaType.TEXT_PLAIN)
                .header(CorrelationIdHolder.CORRELATION_ID_HEADER, passedCorrelationId)).andReturn()
    }

    private MvcResult sendPingWithoutCorrelationId() {
        mockMvc.perform(MockMvcRequestBuilders.get('/ping').accept(MediaType.TEXT_PLAIN)).andReturn()
    }

    private String getCorrelationIdFromResonseHeader(MvcResult mvcResult) {
        mvcResult.getResponse().getHeader(CorrelationIdHolder.CORRELATION_ID_HEADER)
    }
}
