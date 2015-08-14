package com.ofg.infrastructure.healthcheck;

import com.google.common.base.Optional;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ObjectReceiving;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Map;

class PingClient {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ServiceRestClient serviceRestClient;

    public PingClient(ServiceRestClient serviceRestClient) {
        this.serviceRestClient = serviceRestClient;
    }

    public Optional<String> ping(URI uri) {
        final String fullUrl = uri.resolve("/ping").toString();
        try {
            final String response = executeHttpRequest(fullUrl).ofType(String.class);
            return Optional.of(StringUtils.trimToEmpty(response));
        } catch (Exception e) {
            return onException(fullUrl, e);
        }
    }

    public Optional<Map> checkCollaborators(URI uri) {
        String fullUrl = uri.resolve("/collaborators").toString();
        try {
            return Optional.fromNullable(
                    executeHttpRequest(fullUrl).ofType(Map.class));
        } catch (Exception e) {
            return onException(fullUrl, e);
        }
    }

    private ObjectReceiving executeHttpRequest(String url) {
        return serviceRestClient.forExternalService()
                .get().onUrl(url)
                .andExecuteFor().anObject();
    }

    private <T> Optional<T> onException(String fullUrl, Exception e) {
        log.warn("Unable to call {}", fullUrl, e);
        return Optional.absent();
    }

}
