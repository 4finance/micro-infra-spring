package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import com.ofg.infrastructure.discovery.MicroserviceConfiguration
import groovy.text.SimpleTemplateEngine
import groovy.transform.CompileStatic
import org.springframework.http.HttpHeaders

@CompileStatic
public class PredefinedHttpHeaders {

    public static final PredefinedHttpHeaders NO_PREDEFINED_HEADERS = new PredefinedHttpHeaders(null)

    private static final String CONTENT_TYPE_HEADER_NAME = 'Content-Type'
    private final MicroserviceConfiguration.Dependency serviceConfig
    private final SimpleTemplateEngine engine = new SimpleTemplateEngine()

    PredefinedHttpHeaders(MicroserviceConfiguration.Dependency serviceConfig) {
        this.serviceConfig = serviceConfig
    }

    void copyTo(HttpHeaders httpHeaders) {
        if (serviceConfig?.contentTypeTemplate) {
            String contentTypeTemplate = serviceConfig.contentTypeTemplate
            if (serviceConfig.getVersion().isEmpty()) {
                throw new ContentTypeTemplateWithoutVersionException()
            }
            String contentTypeHeader = engine.createTemplate(contentTypeTemplate).make([version: serviceConfig.version])
            httpHeaders.set(CONTENT_TYPE_HEADER_NAME, contentTypeHeader)
        }
        if (serviceConfig?.headers) {
            Map<String, String> predefinedHeaders = serviceConfig.headers
            predefinedHeaders.each {key, value -> httpHeaders.set(key, value)}
        }
    }

    static class ContentTypeTemplateWithoutVersionException extends RuntimeException {}
}
