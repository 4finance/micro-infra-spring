package com.ofg.infrastructure.healthcheck

import com.fasterxml.jackson.annotation.JsonRawValue
import groovy.transform.CompileStatic
import groovy.transform.Memoized
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
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

    @RequestMapping(value = '/${endpoints.microservicejson.id:microservice.json}', method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Json getMicroserviceConfiguration() {
        return new Json(configurationContent())
    }

    @Memoized
    private String configurationContent() {
        return microserviceConfig.inputStream.text
    }

    /**
     * Wrapper class for the content of configuration file.
     */
    public static class Json {

        private final String configuration

        public Json(String configuration) {
            this.configuration = configuration
        }

        @JsonRawValue
        public String configuration() {
            return configuration
        }
    }

}
