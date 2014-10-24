package com.ofg.infrastructure.healthcheck

import com.ofg.infrastructure.discovery.ServiceResolver
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient
import groovy.json.JsonBuilder
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import static org.springframework.web.bind.annotation.RequestMethod.GET

@Slf4j
@RestController
@CompileStatic
@PackageScope
class CollaboratorsConnectivityController {

    private final ServiceResolver serviceResolver
    private final ServiceRestClient serviceRestClient

    @Autowired
    CollaboratorsConnectivityController(ServiceRestClient serviceRestClient, ServiceResolver serviceResolver) {
        this.serviceRestClient = serviceRestClient
        this.serviceResolver = serviceResolver
    }

    @RequestMapping(value = "/collaborators", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String getCollaboratorsConnectivityInfo() {
        Set<String> urls = serviceResolver.fetchAllServiceNames()
        Map collaboratorsState = urls.collectEntries { it -> ["${it}": checkConnectionStatus(it.toString())] }
        JsonBuilder json = new JsonBuilder()
        json collaboratorsState
        return json.toString()
    }

    private String checkConnectionStatus(String serviceName) {
        String pingResult = pingService(serviceName)
        log.info("Connection status checked for service $serviceName with result: '$pingResult'")
        return pingResult == 'OK' ? 'CONNECTED' : 'DISCONNECTED'
    }

    private String pingService(String serviceName) {
        try {
            return serviceRestClient.forService(serviceName)
                    .get()
                    .onUrl("/ping")
                    .andExecuteFor()
                    .anObject()
                    .ofType(String)
        } catch (Exception e) {
            log.error("Unable to ping service '${serviceName}'!", e)
            return 'ERROR'
        }
    }
}
