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
@Slf4j
@RestController
@CompileStatic
@PackageScope
@RequestMapping(value = '/collaborators', method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
class CollaboratorsConnectivityController {

    private final ServiceResolver serviceResolver
    private final PingClient pingClient

    CollaboratorsConnectivityController(ServiceResolver serviceResolver, PingClient pingClient) {
        this.serviceResolver = serviceResolver
        this.pingClient = pingClient
    }

    /**
     * Returns information about connection status of microservice with other microservices.
     * For properly connected service <b>UP</b> state is provided and <b>DOWN</b> otherwise.
     *
     * @return connection status
     */
    @RequestMapping
    Map getCollaboratorsConnectivityInfo() {
        Set<ServicePath> myCollaborators = serviceResolver.fetchCollaboratorsNames()
        return myCollaborators.collectEntries { ServicePath service ->
            return [service.path, statusOfAllCollaboratorInstances(service)]
        }
    }

    private Map<String, String> statusOfAllCollaboratorInstances(ServicePath service) {
        Set<URI> allUrisOfService = serviceResolver.fetchAllUris(service)
        return allUrisOfService.collectEntries { URI instanceUrl ->
            boolean status = checkConnectionStatus(instanceUrl)
            return [instanceUrl, CollaboratorStatus.of(status)]
        }
    }

    @RequestMapping('/all')
    Map getAllCollaboratorsConnectivityInfo() {
        final Set<ServicePath> allServices = serviceResolver.fetchAllServices()
        allServices.collectEntries { ServicePath service ->
            return [service.path, collaboratorsStatusOfAllInstances(service)]
        }
    }

    private Map collaboratorsStatusOfAllInstances(ServicePath service) {
        final Set<URI> collaboratorInstances = serviceResolver.fetchAllUris(service)
        return collaboratorInstances.collectEntries { URI uri ->
            [uri, checkCollaborators(uri)]
        }
    }

    private Map checkCollaborators(URI url) {
        Optional<Map> collaborators = establishCollaboratorsStatus(url)
        return [
                status: CollaboratorStatus.of(collaborators.isPresent()),
                collaborators: collaborators.or([:])
        ]
    }

    private Optional<Map> establishCollaboratorsStatus(URI url) {
        Optional<Map> collaborators = tryCallingCollaborators(url)
        return fallbackWithPingIfCollaboratorsFailed(collaborators, url)
    }

    Optional<Map> fallbackWithPingIfCollaboratorsFailed(Optional<Map> maybeCollaborators, URI url) {
        if (maybeCollaborators.isPresent()) {
            return maybeCollaborators
        }
        return checkConnectionStatus(url) ?
                Optional.of([:]) :
                Optional.absent()
    }

    private Optional<Map> tryCallingCollaborators(URI url) {
        Optional<Map> collaborators = pingClient
                .checkCollaborators(url)
                .transform({ tryAdjustLegacyCollaboratorsResponse(it) })
        return collaborators
    }

    private Map tryAdjustLegacyCollaboratorsResponse(Map collaboratorsResponse) {
        if (isLegacyResponse(collaboratorsResponse)) {
            return adjustLegacyCollaboratorsResponse(collaboratorsResponse)
        } else {
            return collaboratorsResponse;
        }
    }

    private Map adjustLegacyCollaboratorsResponse(Map collaboratorsResponse) {
        collaboratorsResponse.collectEntries {alias, statusStr ->
            final ServicePath path = serviceResolver.resolveAlias(new ServiceAlias(alias as String))
            final Set<URI> allInstances = serviceResolver.fetchAllUris(path)
            CollaboratorStatus status = CollaboratorStatus.of(statusStr == 'CONNECTED')
            return [
                    path.path, allInstances.collectEntries{uri -> [uri, status]}
            ]
        }
    }

    private boolean isLegacyResponse(Map collaboratorsResponse) {
        return !collaboratorsResponse.empty &&
                collaboratorsResponse.values().any{!(it instanceof Map)}
    }

    private boolean checkConnectionStatus(URI url) {
        final GuavaOptional<String> pingResult = pingClient.ping(url)
        return pingResult == GuavaOptional.of('OK')
    }

}
