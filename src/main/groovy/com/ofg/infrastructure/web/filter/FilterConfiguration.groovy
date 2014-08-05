package com.ofg.infrastructure.web.filter

import com.ofg.infrastructure.web.filter.correlationid.CorrelationIdConfiguration
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

import javax.servlet.Filter

@TypeChecked
@Configuration
@Import(CorrelationIdConfiguration)
class FilterConfiguration {

    @Bean
    CORSFilter apiDocsCORSFilter() {
        return new CORSFilter()
    }

    @Bean
    Filter requestBodyLoggingContextFilter(@Value('${request.payload.logging.maxlength:2000}') int maxPayloadLength) {
        return new RequestBodyLoggingContextFilter(maxPayloadLength)
    }

}
