package com.ofg.infrastructure.healthcheck

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * {@link RestController} that responds with OK when server is alive
 */
@RestController
@CompileStatic
@PackageScope
class PingController {

    @RequestMapping(value = "/ping", method = [RequestMethod.GET, RequestMethod.HEAD], produces = MediaType.TEXT_PLAIN_VALUE)
    String ping() {
        return "OK"
    }
}
