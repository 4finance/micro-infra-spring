package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.discovery.watcher.DependencyState
import com.ofg.infrastructure.discovery.watcher.DependencyWatcher
import com.ofg.infrastructure.discovery.watcher.DependencyWatcherListener
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

import static org.springframework.web.bind.annotation.RequestMethod.GET

@Slf4j
@PackageScope
@CompileStatic
@RestController
class CollaboratorsConnectivityController {

    final DependencyWatcher dependencyWatcher
    final Map connectivityState
    final DependencyWatcherListener listener

    @Autowired
    CollaboratorsConnectivityController(DependencyWatcher dependencyWatcher) {
        this.connectivityState = [:]
        this.dependencyWatcher = dependencyWatcher
        log.info('creating dependency watcher listener...')
        this.listener = new DependencyWatcherListener() {
            @Override
            void stateChanged(String dependencyName, DependencyState newState) {
                log.info("dependency watcher listener: dependency changed: $dependencyName $newState")
                connectivityState[dependencyName] = newState
            }
        }
    }

    @PostConstruct
    void init() {
        log.info('listener registered on dependency watcher')
        dependencyWatcher.registerDependencyStateChangeListener(listener)
    }

    @PreDestroy
    void destroy() {
        log.info('listener unregistered')
        dependencyWatcher.unregisterDependencyStateChangeListener(listener)
    }

    @RequestMapping(value = "/collaborators", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String getCollaboratorsConnectivityInfo() {
        return new groovy.json.JsonBuilder(connectivityState).toString()
    }
}
