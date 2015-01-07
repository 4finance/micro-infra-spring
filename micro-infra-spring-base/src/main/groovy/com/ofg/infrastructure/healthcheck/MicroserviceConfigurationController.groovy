package com.ofg.infrastructure.healthcheck

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.transform.Memoized
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * {@link RestController} providing information about configuration of the microservice.
 */
@RestController
@CompileStatic
class MicroserviceConfigurationController {

    private final Resource microserviceConfig

    MicroserviceConfigurationController(Resource microserviceConfig) {
        this.microserviceConfig = microserviceConfig
    }

    @RequestMapping(value = '${endpoints.microservicejson.id:microservice.json}', method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Object getMicroserviceConfiguration() {
        return getContent()
    }

    @Memoized
    private Object getContent() {
        String configurationContent = microserviceConfig.inputStream.text
        return new JsonSlurper().parseText(configurationContent)
    }

}
