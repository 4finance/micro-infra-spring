package com.ofg.infrastructure.web.resttemplate.fluent.common

import com.ofg.infrastructure.web.resttemplate.fluent.FakeTrace
import com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.TracingInfo
import org.springframework.cloud.sleuth.TraceKeys
import org.springframework.web.client.RestOperations
import spock.lang.Specification

class HttpMethodSpec extends Specification {

    protected static final String EVALUATED_SERVICE_URL = 'https://ofg.com.omg:9090'
    protected static final Closure<String> SERVICE_URL = { EVALUATED_SERVICE_URL }
    protected static final String URL_TEMPLATE = '/api/objects/{objectId}'
    protected static final String FULL_URL = EVALUATED_SERVICE_URL + URL_TEMPLATE
    protected static final String PATH = 'api/objects/42'
    protected static final String PATH_WITH_SLASH = "/$PATH"

    protected static final String FULL_SERVICE_URL = "$EVALUATED_SERVICE_URL/$PATH"
    protected static final Long OBJECT_ID = 42L;

    RestOperations restOperations = Mock()
    HttpMethodBuilder httpMethodBuilder
    TracingInfo tracingInfo = new TracingInfo(new FakeTrace(), new TraceKeys())

}
