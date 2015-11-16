package com.ofg.infrastructure.scheduling

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.web.correlationid.CorrelationIdConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration
import org.springframework.cloud.sleuth.instrument.web.TraceWebAutoConfiguration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

@ContextConfiguration(classes = [TraceAutoConfiguration, TraceWebAutoConfiguration,
        ScheduledBeanConfiguration, CorrelationIdConfiguration, BaseConfiguration])
class CorrelationIdOnScheduledMethodSpec extends Specification {

    @Autowired TestBeanWithScheduledMethod beanWithScheduledMethod

    def "should have correlationId set after scheduled method has been called"() {
        PollingConditions conditions = new PollingConditions(timeout: 1.5, initialDelay: 0.1, factor: 1.05)
        expect:
            conditions.eventually {
                beanWithScheduledMethod.correlationId != null
            }
    }

}
