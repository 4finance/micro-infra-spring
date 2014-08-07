package com.ofg.infrastructure.healthcheck

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MvcIntegrationSpec
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration

import static org.springframework.http.MediaType.TEXT_PLAIN
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = [BaseConfiguration, HealthCheckConfiguration], loader = SpringApplicationContextLoader)
class PingControllerMvcSpec extends MvcIntegrationSpec {
    
    def "should return OK on ping for Zabbix"() {
        expect:
            mockMvc.perform(get('/ping').accept(TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string('OK'))
    }
}
