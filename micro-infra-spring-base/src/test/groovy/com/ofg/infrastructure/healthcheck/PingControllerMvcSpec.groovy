package com.ofg.infrastructure.healthcheck

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MvcCorrelationIdSettingIntegrationSpec
import com.ofg.infrastructure.base.ServiceDiscoveryStubbingApplicationConfiguration
import org.junit.ClassRule
import org.junit.contrib.java.lang.system.ProvideSystemProperty
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import spock.lang.Shared

import static org.springframework.http.MediaType.TEXT_PLAIN
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = TestConfig, loader = SpringApplicationContextLoader)
class PingControllerMvcSpec extends MvcCorrelationIdSettingIntegrationSpec {

    @Shared @ClassRule
    public ProvideSystemProperty resolverUrlPropertyIsSet = new ProvideSystemProperty('service.resolver.url', 'localhost:2184');

    def "should return OK on ping for Zabbix"() {
        expect:
            mockMvc.perform(get('/ping').accept(TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string('OK'))
    }

    def "should return OK for HEAD requests from run.sh scripts"() {
        expect:
            mockMvc.perform(head('/ping'))
                    .andExpect(status().isOk())
    }

    /**
     * Create a {@link org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder} for a HEAD request.
     * @param urlTemplate a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    public static MockHttpServletRequestBuilder head(String urlTemplate, Object... urlVariables) {
        return new MockHttpServletRequestBuilder(HttpMethod.HEAD, urlTemplate, urlVariables);
    }

    @Configuration
    @EnableHealthCheck
    @Import([BaseConfiguration, ServiceDiscoveryStubbingApplicationConfiguration])
    static class TestConfig { }

}
