package com.ofg.infrastructure.web.filter
import com.ofg.infrastructure.web.filter.correlationid.CorrelationIdConfiguration
import com.ofg.infrastructure.web.filter.logging.RequestFilterConfiguration
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
/**
 * Configuration that imports {@link CorrelationIdConfiguration} and registers a {@link com.ofg.infrastructure.web.filter.logging.RequestBodyLoggingContextFilter}.
 * 
 * That way your application will automatically 
 * <ul>
 *     <li>set correlation id on received and sent request</li>
 *     <li>if DEUBG level of logging is set on {@link com.ofg.infrastructure.web.filter.logging.RequestBodyLoggingContextFilter} 
 *     print request body in logs - you can limit its length by setting a property</li>
 * </ul>
 * 
 * @see CorrelationIdConfiguration
 * @see RequestFilterConfiguration
 */
@TypeChecked
@Configuration
@Import([CorrelationIdConfiguration, RequestFilterConfiguration])
class FilterConfiguration {    

}
