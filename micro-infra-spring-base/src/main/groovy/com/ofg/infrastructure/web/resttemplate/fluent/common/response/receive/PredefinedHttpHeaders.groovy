package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import static com.ofg.infrastructure.discovery.ServiceConfigurationProperties.*
import groovy.text.SimpleTemplateEngine
import groovy.transform.CompileStatic
import org.springframework.http.HttpHeaders

@CompileStatic
class PredefinedHttpHeaders {

    public static final PredefinedHttpHeaders NO_PREDEFINED_HEADERS = new PredefinedHttpHeaders([:])

    private static final String CONTENT_TYPE_HEADER_NAME = 'Content-Type'
    private final Map serviceConfig
    private final SimpleTemplateEngine engine = new SimpleTemplateEngine()

    PredefinedHttpHeaders(Map serviceConfig) {
        this.serviceConfig = serviceConfig
    }

    void copyTo(HttpHeaders httpHeaders) {
        if (serviceConfig.containsKey(CONTENT_TYPE_TEMPLATE)) {
            String contentTypeTemplate = serviceConfig.get(CONTENT_TYPE_TEMPLATE)
            String contentTypeHeader = engine.createTemplate(contentTypeTemplate).make(serviceConfig)
            httpHeaders.set(CONTENT_TYPE_HEADER_NAME, contentTypeHeader)
        }
        if (serviceConfig.containsKey(HEADERS)) {
            Map<String, String> predefinedHeaders = (Map<String, String>) serviceConfig.get(HEADERS)
            predefinedHeaders.each {key, value -> httpHeaders.set(key, value)}
        }
    }

}
