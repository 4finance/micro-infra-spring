package com.ofg.infrastructure.healthcheck

import com.google.common.base.Function
import com.google.common.base.Optional as GuavaOptional
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j

@Slf4j
@PackageScope
@CompileStatic
class PingClient {

    private final ServiceRestClient serviceRestClient

    PingClient(ServiceRestClient serviceRestClient) {
        this.serviceRestClient = serviceRestClient
    }

    GuavaOptional<String> ping(URI uri) {
        return restCall(uri.toURL(), 'ping', String)
                .transform({ String s -> s.trim() } as Function)
    }

    GuavaOptional<Map> checkCollaborators(URI uri) {
        return restCall(uri.toURL(), 'collaborators', Map)
    }

    private <T> GuavaOptional<T> restCall(URL url, String path, Class<T> type) {
        try {
            String fullUrl = "$url/$path"
            GuavaOptional<T> result = GuavaOptional.of(
                    serviceRestClient.forExternalService()
                            .get()
                            .onUrl(fullUrl)
                            .andExecuteFor()
                            .anObject()
                            .ofType(type))
            log.debug("$fullUrl returned $result")
            return result
        } catch (Exception e) {
            log.warn("Unable to ping service '$url'!", e)
            return GuavaOptional.absent()
        }
    }

}
