package com.ofg.infrastructure.camel

import com.ofg.infrastructure.camel.config.CamelRouteAsBeanConfiguration
import groovy.util.logging.Slf4j
import org.apache.camel.ProducerTemplate
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.impl.DefaultProducerTemplate
import org.apache.camel.impl.InterceptSendToEndpoint
import org.apache.camel.model.ModelCamelContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.sleuth.Tracer
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.test.context.ContextConfiguration
import spock.lang.AutoCleanup
import spock.lang.Specification

import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER
import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.OLD_CORRELATION_ID_HEADER

@Slf4j
@ContextConfiguration(classes = [CamelRouteAsBeanConfiguration, TraceAutoConfiguration, Config])
class AcceptanceSpec extends Specification {

    @Autowired ModelCamelContext camelContext
    @Autowired Tracer tracer
    @AutoCleanup('stop') ProducerTemplate template
    MockEndpoint resultEndpoint
    Random random = new Random()
    static class Config {

        @Bean
        static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
            return new PropertySourcesPlaceholderConfigurer()
        }
    }

    def setup() {
        resultEndpoint =
                ((InterceptSendToEndpoint)camelContext.getEndpoint('mock:result')).getDelegate()
        template = new DefaultProducerTemplate(
                camelContext,
                camelContext.getEndpoint('direct:start'))
        template.start()
    }

    def 'should set correlationId from header of input message'() {
        given:
            Long correlationIdValue = random.nextLong();
        when:
            template.sendBodyAndHeader('<message/>', CORRELATION_ID_HEADER, correlationIdValue)
        then:
            tracer.getCurrentSpan().getTraceId() == correlationIdValue
    }

    def 'should set correlationId from header of input message for old correlation id'() {
        given:
            Long correlationIdValue = random.nextLong();
        when:
            template.sendBodyAndHeader('<message/>', OLD_CORRELATION_ID_HEADER, correlationIdValue)
        then:
            tracer.getCurrentSpan().getTraceId() == correlationIdValue
    }

    def 'should set new correlationId if header in input message is empty'() {
        when:
            template.sendBody('<message/>')
        then:
            tracer.getCurrentSpan().getSpanId() != null
    }

    def 'should set correlationId in output message when it is missing on the input'() {
        when:
            template.sendBody('<message/>')
        then:
            resultEndpoint.message(0).header(CORRELATION_ID_HEADER).isNotNull()
    }

    def 'should copy correlationId from header of input message to the output'() {
        given:
            Long correlationIdValue = random.nextLong();
        when:
            template.sendBodyAndHeader('<message/>', CORRELATION_ID_HEADER, correlationIdValue)
        then:
            resultEndpoint.message(0).header(CORRELATION_ID_HEADER).isEqualTo(correlationIdValue)
    }

    def 'should copy old correlationId from header of input message to the output'() {
        given:
            Long correlationIdValue = random.nextLong();
        when:
            template.sendBodyAndHeader('<message/>', OLD_CORRELATION_ID_HEADER, correlationIdValue)
        then:
            resultEndpoint.message(0).header(OLD_CORRELATION_ID_HEADER).isEqualTo(correlationIdValue)
    }

}
