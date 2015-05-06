package com.ofg.infrastructure.healthcheck

import com.google.common.base.Optional
import com.google.common.base.Optional as GuavaOptional
import com.ofg.infrastructure.discovery.ServiceAlias
import com.ofg.infrastructure.discovery.ServicePath
import com.ofg.infrastructure.discovery.ServiceResolver
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * {@link RestController} providing connection state with services the microservice depends upon.
 */
@RestController
@CompileStatic
@PackageScope
@RequestMapping(value = '/collaborators', method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
class CollaboratorsConnectivityController {

    private final CollaboratorsStatusResolver collaboratorsStatusResolver

    CollaboratorsConnectivityController(CollaboratorsStatusResolver collaboratorsStatusResolver) {
        this.collaboratorsStatusResolver = collaboratorsStatusResolver
    }

    /**
     * Returns information about connection status of microservice with other microservices.
     * For properly connected service <b>UP</b> state is provided and <b>DOWN</b> otherwise.
     *
     * @return connection status
     */
    @RequestMapping
    Map getCollaboratorsConnectivityInfo() {
        return collaboratorsStatusResolver.statusOfMyCollaborators()
    }

    @RequestMapping('/all')
    Map getAllCollaboratorsConnectivityInfo() {
        return collaboratorsStatusResolver.statusOfAllDependencies()
    }
}
