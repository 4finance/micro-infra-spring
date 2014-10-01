package com.ofg.infrastructure.camel

import org.apache.camel.model.InterceptFromDefinition
import spock.lang.Specification

class CorrelationIdRouteBuilderSpec extends Specification {

    def "builder defines correlationId interception on inbound messages"() {
        given:
        def builder = new CorrelationIdRouteBuilder()

        when:
        builder.configure()

        then:
        List<InterceptFromDefinition> interceptFroms = builder.getRouteCollection().interceptFroms
        interceptFroms.each {it.processRef()}

        true
    }

}
