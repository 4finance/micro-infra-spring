package com.ofg.infrastructure.camel

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.camel.CamelContext
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.ModelCamelContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

@Slf4j
@Service
@CompileStatic
class CamelRouteModifier {

    CamelContext camelContext

    @Autowired
    CamelRouteModifier(CamelContext camelContext) {
        this.camelContext = camelContext
    }

    @PostConstruct
    void addCorrelationIdInterception() {
        log.debug('Modifying routes defined in Camel context...')

        camelContext.getRouteDefinitions().each {
            it.adviceWith(camelContext, new RouteBuilder() {
                @Override
                void configure() throws Exception {
                    Processor correlationIdInterceptor = new CorrelationIdInterceptor()
                    interceptFrom().process(correlationIdInterceptor)
                    interceptSendToEndpoint("*").process(correlationIdInterceptor)
                }
            })
        }
    }

}
