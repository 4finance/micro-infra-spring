package com.ofg.infrastructure.healthcheck;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping("/ping")
    public ResponseEntity<String> getCollaboratorsPing(@RequestParam(value = "serviceId") String serviceId) {
        String status = collaboratorsStatusResolver.statusOfService(serviceId);
        if ("UP".equals(status)) {
            return ResponseEntity.ok("OK");
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/all")
    public Map getAllCollaboratorsConnectivityInfo() {
        return collaboratorsStatusResolver.statusOfAllDependencies();
    }

    private final CollaboratorsStatusResolver collaboratorsStatusResolver;
}
