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

/**
 * {@link RestController} providing connection state with services the microservice depends upon.
 */
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

    /**
     * Returns information about connection status of microservice with other microservices.
     * For properly connected service <b>CONNECTED</b> state is provided and <b>DISCONNECTED</b> otherwise.
     *
     * @return connection status
     */
    @RequestMapping(value = "/collaborators", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String getCollaboratorsConnectivityInfo() {
        Set<String> collaborators = serviceResolver.fetchCollaboratorsNames()
        Map collaboratorsState = collaborators.collectEntries { String collaborator -> ["$collaborator": checkConnectionStatus(collaborator)] }
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
