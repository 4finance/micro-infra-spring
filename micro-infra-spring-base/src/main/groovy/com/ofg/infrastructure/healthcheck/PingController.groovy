package com.ofg.infrastructure.healthcheck

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE
import static org.springframework.web.bind.annotation.RequestMethod.GET
import static org.springframework.web.bind.annotation.RequestMethod.HEAD

/**
 * {@link RestController} that responds with OK when server is alive
 */
@RestController
@CompileStatic
@PackageScope
class PingController {

    @RequestMapping(value = "/ping", method = [GET, HEAD], produces = TEXT_PLAIN_VALUE)
    String ping() {
        return "OK"
    }
}
