package com.ofg.infrastructure.camel
import com.ofg.infrastructure.camel.config.CamelRouteAsBeanConfiguration
import com.ofg.infrastructure.correlationid.CorrelationIdHolder
import com.ofg.infrastructure.web.correlationid.CorrelationIdConfiguration
import groovy.util.logging.Slf4j
import org.apache.camel.ProducerTemplate
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.impl.DefaultProducerTemplate
import org.apache.camel.impl.InterceptSendToEndpoint
import org.apache.camel.model.ModelCamelContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.sleuth.Trace
import org.springframework.context.annotation.Bean
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.test.context.ContextConfiguration
import spock.lang.AutoCleanup
import spock.lang.Specification

@Slf4j
@ContextConfiguration(classes = [CamelRouteAsBeanConfiguration, CorrelationIdConfiguration, Config])
class AcceptanceSpec extends Specification {

    @Autowired ModelCamelContext camelContext
    @AutoCleanup('stop') ProducerTemplate template
    MockEndpoint resultEndpoint

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
            String correlationIdValue = UUID.randomUUID().toString()
        when:
            template.sendBodyAndHeader('<message/>', Trace.TRACE_ID_NAME, correlationIdValue)
        then:
            CorrelationIdHolder.get().traceId == correlationIdValue
    }

    def 'should set new correlationId if header in input message is empty'() {
        when:
            template.sendBody('<message/>')
        then:
            CorrelationIdHolder.get().traceId != null
    }

    def 'should set correlationId in output message when it is missing on the input'() {
        when:
            template.sendBody('<message/>')
        then:
            resultEndpoint.message(0).header(Trace.TRACE_ID_NAME).isNotNull()
    }

    def 'should copy correlationId from header of input message to the output'() {
        given:
            String correlationIdValue = UUID.randomUUID().toString()
        when:
            template.sendBodyAndHeader('<message/>', Trace.TRACE_ID_NAME, correlationIdValue)
        then:
            resultEndpoint.message(0).header(Trace.TRACE_ID_NAME).isEqualTo(correlationIdValue)
    }

}
