package com.ofg.infrastructure.healthcheck

import com.google.common.base.Optional
import com.ofg.infrastructure.discovery.ServiceAlias
import com.ofg.infrastructure.discovery.ServicePath
import com.ofg.infrastructure.discovery.ServiceResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Slf4j
class CollaboratorsStatusResolver {

    private final ServiceResolver serviceResolver
    private final PingClient pingClient

    @Autowired
    CollaboratorsStatusResolver(ServiceResolver serviceResolver, PingClient pingClient) {
        this.serviceResolver = serviceResolver
        this.pingClient = pingClient
    }

    public Map statusOfMyCollaborators() {
        Set<ServicePath> myCollaborators = serviceResolver.fetchMyDependencies()
        return myCollaborators.collectEntries { ServicePath service ->
            return [service.path, statusOfAllCollaboratorInstances(service)]
        }
    }

    public String statusOfService(String serviceId) {
        Set<ServicePath> myCollaborators = serviceResolver.fetchMyDependencies()
        ServicePath service = myCollaborators
                .find { ServicePath service -> service.lastName == serviceId }
        return pingOfAllCollaboratorInstances(service)
    }

    public Map statusOfAllDependencies() {
        final Set<ServicePath> allServices = serviceResolver.fetchAllDependencies()
        return allServices.collectEntries { ServicePath service ->
            return [service.path, collaboratorsStatusOfAllInstances(service)]
        }
    }

    private Map<String, String> statusOfAllCollaboratorInstances(ServicePath service) {
        Set<URI> allUrisOfService = serviceResolver.fetchAllUris(service)
        return allUrisOfService.collectEntries { URI instanceUrl ->
            boolean status = checkConnectionStatus(instanceUrl)
            return [instanceUrl, CollaboratorStatus.of(status)]
        }
    }

    private String pingOfAllCollaboratorInstances(ServicePath service) {
        Set<URI> allUrisOfService = serviceResolver.fetchAllUris(service)
        boolean status = allUrisOfService
                .collect { URI instanceUrl -> checkConnectionStatus(instanceUrl) }
                .find { status -> status }
        return CollaboratorStatus.of(status)
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
                status       : CollaboratorStatus.of(collaborators.isPresent()),
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
        Map result = [:]
        collaboratorsResponse.each { alias, statusStr ->
            tryResolveAlias(alias as String).transform({ ServicePath path ->
                result << suspectedStatusOfAllInstances(path, statusStr as String)
            })
        }
        return result
    }

    private Map suspectedStatusOfAllInstances(ServicePath service, String statusStr) {
        final Set<URI> allInstances = serviceResolver.fetchAllUris(service)
        CollaboratorStatus status = CollaboratorStatus.of(statusStr == 'CONNECTED')
        return [
                (service.path): allInstances.collectEntries { uri -> [uri, status] }
        ]
    }

    private Optional<ServicePath> tryResolveAlias(String alias) {
        try {
            ServiceAlias serviceAlias = new ServiceAlias(alias as String)
            return Optional.of(serviceResolver.resolveAlias(serviceAlias))
        } catch (NoSuchElementException e) {
            log.warn("Unable to resolve alias $alias", e)
            return Optional.absent()
        }
    }

    private boolean isLegacyResponse(Map collaboratorsResponse) {
        return !collaboratorsResponse.empty &&
                collaboratorsResponse.values().any { !(it instanceof Map) }
    }

    private boolean checkConnectionStatus(URI url) {
        return pingClient.ping(url).isPresent()
    }

}
