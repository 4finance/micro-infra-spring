package com.ofg.infrastructure.healthcheck
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.infrastructure.discovery.SpringCloudToMicroserviceJsonConverter
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.transform.Memoized
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

    private final ServiceConfigurationResolver serviceConfigurationResolver
    private final SpringCloudToMicroserviceJsonConverter springCloudToMicroserviceJsonConverter;

    MicroserviceConfigurationController(ServiceConfigurationResolver serviceConfigurationResolver, SpringCloudToMicroserviceJsonConverter springCloudToMicroserviceJsonConverter) {
        this.serviceConfigurationResolver = serviceConfigurationResolver;
        this.springCloudToMicroserviceJsonConverter = springCloudToMicroserviceJsonConverter;
    }

    @RequestMapping(value = '${endpoints.microservicejson.id:microservice.json}', method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Deprecated
    Object getMicroserviceConfigurationDeprecated() {
        return getContent()
    }

    @RequestMapping(value = 'microserviceDescriptor', method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Object getMicroserviceConfiguration() {
        return getContent()
    }

    @Memoized
    private Object getContent() {
        if (serviceConfigurationResolver != null) {
            return new JsonSlurper().parseText(serviceConfigurationResolver.configurationAsString)
        }
        return springCloudToMicroserviceJsonConverter.toMicroserviceJsonNotation();
    }

}
