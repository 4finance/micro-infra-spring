package com.ofg.infrastructure.healthcheck;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * {@link RestController} providing connection state with services the microservice depends upon.
 */
@RestController
@RequestMapping(value = "/collaborators", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
class CollaboratorsConnectivityController {
    public CollaboratorsConnectivityController(CollaboratorsStatusResolver collaboratorsStatusResolver) {
        this.collaboratorsStatusResolver = collaboratorsStatusResolver;
    }

    /**
     * Returns information about connection status of microservice with other microservices.
     * For properly connected service <b>UP</b> state is provided and <b>DOWN</b> otherwise.
     *
     * @return connection status
     */
    @RequestMapping
    public Map getCollaboratorsConnectivityInfo() {
        return collaboratorsStatusResolver.statusOfMyCollaborators();
    }

    @RequestMapping("/all")
    public Map getAllCollaboratorsConnectivityInfo() {
        return collaboratorsStatusResolver.statusOfAllDependencies();
    }

    private final CollaboratorsStatusResolver collaboratorsStatusResolver;
}
