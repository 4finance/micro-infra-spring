package com.ofg.infrastructure.web.filter

import com.ofg.infrastructure.web.filter.correlationid.CorrelationIdConfiguration
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * Configuration that imports {@link CorrelationIdConfiguration}.
 * 
 * That way your application will automatically set correlation id on received and sent request.
 * 
 * @see CorrelationIdConfiguration
 */
@TypeChecked
@Configuration
@Import([CorrelationIdConfiguration])
class FilterConfiguration {    
}
