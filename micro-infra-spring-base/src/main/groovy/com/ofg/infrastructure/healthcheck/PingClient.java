package com.ofg.infrastructure.healthcheck;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

class PingClient {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ServiceRestClient serviceRestClient;

    public PingClient(ServiceRestClient serviceRestClient) {
        this.serviceRestClient = serviceRestClient;
    }

    public Optional<String> ping(URI uri) {
        try {
            return restCall(uri.toURL(), "ping", String.class).transform(new Function<String, String>() {
                public String apply(String s) {
                    return s.trim();
                }
            });
        } catch (MalformedURLException e) {
            log.error("Exception occurred while trying to perform a rest call", e);
            return Optional.absent();
        }
    }

    public Optional<Map> checkCollaborators(URI uri) {
        try {
            return restCall(uri.toURL(), "collaborators", Map.class);
        } catch (MalformedURLException e) {
            log.error("Exception occurred while trying to perform a rest call", e);
            return Optional.absent();
        }
    }

    private <T> Optional<T> restCall(URL url, String path, Class<T> type) {
        try {
            String fullUrl = String.valueOf(url) + "/" + path;
            Optional<T> result = Optional.fromNullable(serviceRestClient.forExternalService()
                                                                        .get()
                                                                        .onUrl(fullUrl)
                                                                        .andExecuteFor()
                                                                        .anObject()
                                                                        .ofType(type));
            log.debug(fullUrl + " returned " + String.valueOf(result));
            return result;
        } catch (Exception e) {
            log.warn("Unable to ping service [" + String.valueOf(url) + "]!", e);
            return Optional.absent();
        }
    }
}
