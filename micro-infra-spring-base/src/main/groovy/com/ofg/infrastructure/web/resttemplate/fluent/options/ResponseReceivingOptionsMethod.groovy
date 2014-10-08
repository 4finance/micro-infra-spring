package com.ofg.infrastructure.web.resttemplate.fluent.options
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.Executable
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersHaving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HttpEntitySending
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ResponseExtracting

/**
 * {@link org.springframework.http.HttpMethod#OPTIONS} HTTP method allows receiving requests with body what 
 * {@link com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ResponseExtracting} interface provides.
 * Additionally it gives the possibility to easily retrieve the {@link org.springframework.http.HttpHeaders#ALLOW}
 * header via {@link AllowHeaderReceiving} interface.
 */
interface ResponseReceivingOptionsMethod extends
        HeadersHaving<ResponseReceivingOptionsMethod>, ResponseExtracting, AllowHeaderReceiving,
        Executable<ResponseReceivingOptionsMethod>, HttpEntitySending<ResponseReceivingOptionsMethod> {

}
