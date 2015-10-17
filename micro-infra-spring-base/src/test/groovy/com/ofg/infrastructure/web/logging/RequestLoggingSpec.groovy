package com.ofg.infrastructure.web.logging

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MicroserviceMvcWiremockSpec
import com.ofg.infrastructure.discovery.web.HttpMockServer
import com.ofg.infrastructure.web.logging.config.LogsConfig
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClientConfiguration
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.transform.TypeChecked
import org.junit.ClassRule
import org.junit.contrib.java.lang.system.ProvideSystemProperty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import spock.lang.Shared


@ContextConfiguration(classes = [BaseConfiguration, ServiceRestClientConfiguration], loader = SpringApplicationContextLoader)
class RequestLoggingSpec extends MicroserviceMvcWiremockSpec {


    @Shared @ClassRule
    public ProvideSystemProperty resolverUrlPropertyIsSet = new ProvideSystemProperty('service.resolver.url', 'localhost:2193');


    @CompileStatic
    @Configuration
    @Import([RequestLoggingTestingController,LogsConfig])
    @EnableRequestBodyLogging
    static class RequestLoggingSpecConfiguration {
    }

    @RestController
    @TypeChecked
    @PackageScope
    static class RequestLoggingTestingController {

        @Autowired private ServiceRestClient serviceRestClient
        @Autowired private HttpMockServer httpMockServer

        @RequestMapping(value = "/syncPing", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
        String syncPing() {
            callWiremockAndReturnOk()
        }

        private String callWiremockAndReturnOk() {
            serviceRestClient.forExternalService()
                    .get()
                    .onUrl(new URI("http://localhost:${httpMockServer.port()}"))
                    .andExecuteFor()
                    .aResponseEntity()
                    .ofType(String)
            return "OK"
        }
    }
}
