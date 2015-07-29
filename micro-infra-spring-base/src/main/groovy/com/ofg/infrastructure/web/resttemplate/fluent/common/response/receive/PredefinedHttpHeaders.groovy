package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import com.ofg.infrastructure.discovery.MicroserviceConfiguration
import groovy.text.SimpleTemplateEngine
import groovy.transform.CompileStatic
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies
import org.springframework.http.HttpHeaders

@CompileStatic
public class PredefinedHttpHeaders {

    public static final PredefinedHttpHeaders NO_PREDEFINED_HEADERS = new PredefinedHttpHeaders()

    private static final String CONTENT_TYPE_HEADER_NAME = 'Content-Type'
    @Deprecated private final MicroserviceConfiguration.Dependency serviceConfig
    private final ZookeeperDependencies.ZookeeperDependency zookeeperDependency;
    private final SimpleTemplateEngine engine = new SimpleTemplateEngine()

    @Deprecated
    PredefinedHttpHeaders(MicroserviceConfiguration.Dependency serviceConfig) {
        this.serviceConfig = serviceConfig
        this.zookeeperDependency = null;
    }

    PredefinedHttpHeaders(ZookeeperDependencies.ZookeeperDependency zookeeperDependency) {
        this.serviceConfig = null
        this.zookeeperDependency = zookeeperDependency;
    }

    PredefinedHttpHeaders() {
        this.serviceConfig = null
        this.zookeeperDependency = null
    }

    void copyTo(HttpHeaders httpHeaders) {
        if (serviceConfig != null) {
            copyUsingServiceConfig(httpHeaders)
        } else if (zookeeperDependency != null) {
            copyUsingZookeeperDeps(httpHeaders)
        }
    }

    @Deprecated
    private void copyUsingServiceConfig(HttpHeaders httpHeaders) {
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
            predefinedHeaders.each { key, value -> httpHeaders.set(key, value) }
        }
    }

    private void copyUsingZookeeperDeps(HttpHeaders httpHeaders) {
        if (zookeeperDependency.contentTypeTemplate) {
            String contentTypeTemplate = zookeeperDependency.contentTypeTemplate
            if (zookeeperDependency.getVersion().isEmpty()) {
                throw new ContentTypeTemplateWithoutVersionException()
            }
            String contentTypeHeader = engine.createTemplate(contentTypeTemplate).make([version: zookeeperDependency.version])
            httpHeaders.set(CONTENT_TYPE_HEADER_NAME, contentTypeHeader)
        }
        if (zookeeperDependency?.headers) {
            Map<String, String> predefinedHeaders = zookeeperDependency.headers
            predefinedHeaders.each { key, value -> httpHeaders.set(key, value) }
        }
    }

    static class ContentTypeTemplateWithoutVersionException extends RuntimeException {}
}
