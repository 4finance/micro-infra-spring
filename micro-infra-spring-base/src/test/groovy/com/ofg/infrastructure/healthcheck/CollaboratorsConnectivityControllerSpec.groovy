package com.ofg.infrastructure.healthcheck

import com.google.common.base.Optional as GuavaOptional
import com.ofg.infrastructure.discovery.ServiceAlias
import com.ofg.infrastructure.discovery.ServicePath
import com.ofg.infrastructure.discovery.ServiceResolver
import org.apache.commons.lang.StringUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

import static com.ofg.infrastructure.healthcheck.CollaboratorStatus.DOWN
import static com.ofg.infrastructure.healthcheck.CollaboratorStatus.UP

class CollaboratorsConnectivityControllerSpec extends Specification {

    public static final URI MICRO_1A_URL = 'http://micro1a:8080'.toURI()
    public static final URI MICRO_1B_URL = 'http://micro1b:8080'.toURI()
    public static final URI MICRO_2_URL = 'http://micro2:8080'.toURI()
    public static final URI MICRO_3A_URL = 'http://micro3a:8080'.toURI()
    public static final URI MICRO_3B_URL = 'http://micro3b:8080'.toURI()
    public static final URI MICRO_3C_URL = 'http://micro3c:8080'.toURI()
    public static final URI MICRO_4_URL = 'http://micro4:8080'.toURI()

    def serviceResolverMock = Mock(ServiceResolver)
    def pingClientMock = Mock(PingClient)

    def controller = new CollaboratorsConnectivityController(new CollaboratorsStatusResolver(serviceResolverMock, pingClientMock))

    def 'should return empty list of collaborators'() {
        given:
            iHaveNoCollaborators()

        when:
            Map info = controller.collaboratorsConnectivityInfo

        then:
            info.isEmpty()
    }

    private Object iHaveNoCollaborators() {
        return myCollaboratorsAre()
    }

    private noMicroservices() {
        instancesOfMicroservices([:])
    }

    def 'should return status of collaborators with multiple instances'() {
        given:
            instancesOfMicroservices(['/com/micro1': [MICRO_1A_URL, MICRO_1B_URL],
                                      '/com/micro2': [MICRO_2_URL],
                                      '/com/micro3': [MICRO_3A_URL]])
            myCollaboratorsAre('/com/micro1', '/com/micro2')
            theseAreOk(MICRO_2_URL, MICRO_1A_URL)

        when:
            Map info = controller.collaboratorsConnectivityInfo

        then:
            info.size() == 2
            info['/com/micro1'] == [
                    (MICRO_1A_URL): UP,
                    (MICRO_1B_URL): DOWN
            ]
            info['/com/micro2'] == [
                    (MICRO_2_URL): UP
            ]
    }

    def 'should return status OK when selected collaborator is UP'() {
        given:
            instancesOfMicroservices(['/com/micro1': [MICRO_1A_URL, MICRO_1B_URL],
                                      '/com/micro2': [MICRO_2_URL],
                                      '/com/micro3': [MICRO_3A_URL]])
            myCollaboratorsAre('/com/micro1', '/com/micro2')
            theseAreOk(MICRO_2_URL, MICRO_1A_URL)

        when:
            ResponseEntity<String> response = controller.getCollaboratorsPing('micro1')

        then:
            println response.body
            response.statusCode == HttpStatus.OK
            response.body == 'OK'
    }

    def 'should return 404 when selected collaborator is DOWN'() {
        given:
            instancesOfMicroservices(['/com/micro1': [MICRO_1A_URL, MICRO_1B_URL],
                                      '/com/micro2': [MICRO_2_URL],
                                      '/com/micro3': [MICRO_3A_URL]])
            myCollaboratorsAre('/com/micro1', '/com/micro2')
            theseAreOk(MICRO_2_URL, MICRO_1A_URL)

        when:
            ResponseEntity<String> response = controller.getCollaboratorsPing('micro3')

        then:
            println response.body
            response.statusCode == HttpStatus.NOT_FOUND
    }

    def myCollaboratorsAre(String... strings) {
        serviceResolverMock.fetchMyDependencies() >> strings.collect { new ServicePath(it) }.toList().toSet()
    }

    private void instancesOfMicroservices(Map<String, List<String>> instances) {
        serviceResolverMock.fetchAllDependencies() >> instances.keySet().collect { new ServicePath(it) }.toSet()
        instances.each { path, urls ->
            final ServicePath wrappedPath = new ServicePath(path)
            final ServiceAlias alias = new ServiceAlias(StringUtils.substringAfterLast(path, '/'))
            serviceResolverMock.resolveAlias(alias) >> wrappedPath
            serviceResolverMock.fetchAllUris(wrappedPath) >> urls.toSet()
        }
    }

    private void theseAreOk(URI... urls) {
        urls.each {
            pingClientMock.ping(it) >> GuavaOptional.of('OK')
        }
        pingClientMock.ping(_) >> GuavaOptional.absent()
    }

    def 'should return empty map of all collaborators in the system'() {
        given:
            noMicroservices()

        when:
            Map info = controller.allCollaboratorsConnectivityInfo

        then:
            info.isEmpty()
    }

    def 'should return map about connectivity between each and every microservice'() {
        given:
            instancesOfMicroservices(['/com/micro1': [MICRO_1A_URL, MICRO_1B_URL],
                                      '/com/micro2': [MICRO_2_URL],
                                      '/com/micro3': [MICRO_3A_URL, MICRO_3B_URL, MICRO_3C_URL],
                                      '/com/micro4': [MICRO_4_URL]])
            collaboratorsStatusOf(MICRO_1A_URL, ['/com/micro2': [(MICRO_2_URL): UP]])
            collaboratorsStatusOf(MICRO_1B_URL, ['/com/micro2': [(MICRO_2_URL): UP]])
            collaboratorsStatusOf(MICRO_2_URL, [
                    '/com/micro3': [
                            (MICRO_3A_URL): UP,
                            (MICRO_3B_URL): DOWN,
                            (MICRO_3C_URL): UP],
                    '/com/micro4': [(MICRO_4_URL): UP]])
            collaboratorsCheckFailed(MICRO_3A_URL)
            noCollaboratorsOfRemainingServices()

        when:
            Map info = controller.allCollaboratorsConnectivityInfo

        then:
            info.size() == 4
            info['/com/micro1'].size() == 2
            info['/com/micro1'][MICRO_1A_URL] == [
                    status       : UP,
                    collaborators: [
                            '/com/micro2': [
                                    (MICRO_2_URL): UP]]]
            info['/com/micro1'][MICRO_1B_URL] == [
                    status       : UP,
                    collaborators: [
                            '/com/micro2': [
                                    (MICRO_2_URL): UP]]]
            info['/com/micro2'] == [
                    (MICRO_2_URL): [
                            status       : UP,
                            collaborators: [
                                    '/com/micro3': [
                                            (MICRO_3A_URL): UP,
                                            (MICRO_3B_URL): DOWN,
                                            (MICRO_3C_URL): UP],
                                    '/com/micro4': [
                                            (MICRO_4_URL): UP]]]]
            info['/com/micro3'] == [
                    (MICRO_3A_URL): [
                            status       : DOWN,
                            collaborators: [:]],
                    (MICRO_3B_URL): [
                            status       : UP,
                            collaborators: [:]],
                    (MICRO_3C_URL): [
                            status       : UP,
                            collaborators: [:]]]
            info['/com/micro4'] == [
                    (MICRO_4_URL): [
                            status       : UP,
                            collaborators: [:]]]
    }

    private void noCollaboratorsOfRemainingServices() {
        pingClientMock.checkCollaborators(_) >> GuavaOptional.of([:])
    }

    private void collaboratorsCheckFailed(URI url) {
        pingClientMock.checkCollaborators(url) >> GuavaOptional.absent()
        pingClientMock.ping(url) >> GuavaOptional.absent()
    }

    private void collaboratorsStatusOf(URI url, Map status) {
        pingClientMock.checkCollaborators(url) >> GuavaOptional.of(status)
    }

    def 'should not fail when microservice has no instances'() {
        given:
            instancesOfMicroservices(['/com/micro1': []])

        when:
            Map info = controller.allCollaboratorsConnectivityInfo

        then:
            info['/com/micro1'] == [:]
    }

    def 'should adjust collaborators response from micro-infra-spring version < 0.7.4'() {
        given:
            instancesOfMicroservices(['/com/micro1': [MICRO_1A_URL],
                                      '/com/micro3': [MICRO_3A_URL, MICRO_3B_URL]])
            serviceReturnsLegacyCollaboratorsFormat(MICRO_1A_URL)
            noCollaboratorsOfRemainingServices()

        when:
            Map info = controller.allCollaboratorsConnectivityInfo

        then:
            info.size() == 2
            info['/com/micro1'] == [
                    (MICRO_1A_URL): [
                            status       : UP,
                            collaborators: [
                                    '/com/micro3': [
                                            (MICRO_3A_URL): UP,
                                            (MICRO_3B_URL): UP]]]]
            info['/com/micro3'] == [
                    (MICRO_3A_URL): [
                            status       : UP,
                            collaborators: [:]],
                    (MICRO_3B_URL): [
                            status       : UP,
                            collaborators: [:]]]
    }

    def 'should ignore unresolvable aliases coming from legacy /collaborators response'() {
        given:
            instancesOfMicroservices(['/com/micro1': [MICRO_1A_URL]])
            serviceReturnsLegacyCollaboratorsFormat(MICRO_1A_URL)
            noCollaboratorsOfRemainingServices()
            unresolvableAlias('micro3')

        when:
            Map info = controller.allCollaboratorsConnectivityInfo

        then:
            info.size() == 1
            info['/com/micro1'] == [
                    (MICRO_1A_URL): [
                            status       : UP,
                            collaborators: [:]]]
    }

    def 'when /collaborators is unavailable, try to at least call /ping and degrade response gracefully'() {
        given:
            instancesOfMicroservices(['/com/micro1': [MICRO_1A_URL]])
            theseAreOk(MICRO_1A_URL)
        and:
            collaboratorsCheckFailed(MICRO_1A_URL)

        when:
            Map info = controller.allCollaboratorsConnectivityInfo

        then:
            info.size() == 1
            info['/com/micro1'] == [
                    (MICRO_1A_URL): [
                            status       : UP,
                            collaborators: [:]]]
    }

    private def serviceReturnsLegacyCollaboratorsFormat(URI uri) {
        collaboratorsStatusOf(uri, ['micro3': 'CONNECTED'])
    }

    private def unresolvableAlias(String alias) {
        serviceResolverMock.resolveAlias(new ServiceAlias(alias)) >> {throw new NoSuchElementException(alias)}
    }

}
